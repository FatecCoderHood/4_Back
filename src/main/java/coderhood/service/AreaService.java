package coderhood.service;

import coderhood.dto.*;
import coderhood.exception.MessageException;
import coderhood.model.Area;
import coderhood.model.StatusArea;
import coderhood.model.Talhao;
import coderhood.repository.AreaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public Area createArea(AreaDto areaDTO) {
        log.info("Criando nova área básica");
        Area area = new Area();
        area.setNome(areaDTO.getNome());
        area.setEstado(areaDTO.getEstado());
        area.setCidade(areaDTO.getCidade());
        area.setStatus(areaDTO.getStatus() != null ? areaDTO.getStatus() : StatusArea.EM_ANALISE);
        log.debug("Área criada (ainda não salva): {}", area);
        Area savedArea = areaRepository.save(area);
        log.info("Área salva com ID: {}", savedArea.getId());
        return savedArea;
    }

    public Area createAreaWithGeoJson(AreaGeoJsonDto areaDTO) {
        log.info("Iniciando criação de área com GeoJSON");
        log.debug("DTO recebido: {}", areaDTO);

        Area area = new Area();
        area.setNome(areaDTO.getNome());
        area.setEstado(areaDTO.getEstado());
        area.setCidade(areaDTO.getCidade());
        area.setStatus(StatusArea.EM_ANALISE);

        if (areaDTO.getGeojson() != null) {
            log.info("Processando GeoJSON principal");
            List<Talhao> talhoes = processarGeoJson(areaDTO.getGeojson());
            talhoes.forEach(area::addTalhao);
            log.info("{} talhões adicionados", talhoes.size());
        }

        if (areaDTO.getErvasDaninhasGeojson() != null) {
            log.info("Processando GeoJSON de ervas daninhas");
            processarErvasDaninhasGeoJson(area, areaDTO.getErvasDaninhasGeojson());
        } else {
            log.warn("Nenhum GeoJSON de ervas daninhas fornecido");
        }

        if (areaDTO.getProdutividadePorAno() != null) {
            log.info("Processando produtividade por ano");
            areaDTO.getProdutividadePorAno().forEach((mnTl, produtividade) -> {
                area.getTalhoes().stream()
                        .filter(t -> mnTl.equals(t.getMnTl().toString()))
                        .findFirst()
                        .ifPresent(t -> {
                            log.debug("Definindo produtividade {} para talhão {}", produtividade, mnTl);
                            t.setProdutividadePorAno(produtividade);
                        });
            });
        }

        log.info("Salvando área no banco de dados");
        Area savedArea = areaRepository.save(area);
        log.info("Área criada com ID: {}", savedArea.getId());
        log.debug("Área salva: {}", savedArea);

        return savedArea;
    }

    @SuppressWarnings("unchecked")
    private List<Talhao> processarGeoJson(Map<String, Object> geojson) {
        log.info("Processando GeoJSON principal");
        try {
            if (!(geojson.get("features") instanceof List)) {
                throw new MessageException("Formato GeoJSON inválido - 'features' deve ser uma lista");
            }

            List<Map<String, Object>> features = (List<Map<String, Object>>) geojson.get("features");
            log.debug("Número de features encontradas: {}", features.size());

            return features.stream().map(feature -> {
                Map<String, Object> properties = (Map<String, Object>) feature.getOrDefault("properties",
                        new HashMap<>());
                log.debug("Processando feature com properties: {}", properties);

                Talhao talhao = new Talhao();
                talhao.setMnTl(getIntegerProperty(properties, "MN_TL", "mnTl"));
                talhao.setAreaHaTl(getDoubleProperty(properties, "AREA_HA_TL", "areaHaTl"));
                talhao.setSolo(getStringProperty(properties, "SOLO", "solo"));
                talhao.setCultura(getStringProperty(properties, "CULTURA", "cultura"));
                talhao.setSafra(getStringProperty(properties, "SAFRA", "safra"));

                Double produtividade = getDoubleProperty(properties, "PRODUTIVIDADE", "produtividadePorAno");
                talhao.setProdutividadePorAno(produtividade != null ? produtividade : 0.0);
                log.debug("Produtividade definida para: {}", talhao.getProdutividadePorAno());

                talhao.setGeojson(feature.toString());
                log.debug("Talhão criado: {}", talhao);
                return talhao;
            }).collect(Collectors.toList());
        } catch (ClassCastException e) {
            log.error("Erro de tipo no GeoJSON", e);
            throw new MessageException("Erro de tipo no GeoJSON: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar GeoJSON", e);
            throw new MessageException("Erro ao processar GeoJSON: " + e.getMessage());
        }
    }

    private void processarErvasDaninhasGeoJson(Area area, Map<String, Object> ervasDaninhasGeojson) {
        log.info("Iniciando processamento de GeoJSON de ervas daninhas");

        try {
            if (ervasDaninhasGeojson == null) {
                log.warn("GeoJSON de ervas daninhas é nulo");
                return;
            }

            if (!(ervasDaninhasGeojson.get("features") instanceof List)) {
                log.error("Formato inválido - features não é uma lista");
                throw new MessageException("Formato GeoJSON de ervas daninhas inválido");
            }

            List<Map<String, Object>> features = (List<Map<String, Object>>) ervasDaninhasGeojson.get("features");

            Map<Integer, List<Map<String, Object>>> featuresPorTalhao = new HashMap<>();

            for (Map<String, Object> feature : features) {
                Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
                if (properties == null) {
                    log.warn("Feature sem properties - ignorando");
                    continue;
                }

                Integer nmTl = getIntegerProperty(properties, "NM_TL", "nmTl");
                if (nmTl == null) {
                    log.warn("Feature sem NM_TL - ignorando");
                    continue;
                }

                String classe = getStringProperty(properties, "CLASSE", "classe");
                if (!"DANINHA".equalsIgnoreCase(classe)) {
                    continue;
                }

                featuresPorTalhao.computeIfAbsent(nmTl, k -> new ArrayList<>())
                        .add(feature);
            }

            featuresPorTalhao.forEach((nmTl, featureList) -> {
                area.getTalhoes().stream()
                        .filter(t -> nmTl.equals(t.getMnTl()))
                        .findFirst()
                        .ifPresentOrElse(
                                talhao -> {
                                    List<String> geojsonFeatures = featureList.stream()
                                            .map(f -> f.toString())
                                            .collect(Collectors.toList());

                                    log.debug("Adicionando {} features de ervas daninhas ao talhão {}",
                                            geojsonFeatures.size(), nmTl);
                                    talhao.setErvasDaninhas(geojsonFeatures);
                                },
                                () -> log.warn("Talhão {} não encontrado para associar ervas daninhas", nmTl));
            });

        } catch (Exception e) {
            log.error("Erro ao processar GeoJSON de ervas daninhas", e);
            throw new MessageException("Erro ao processar ervas daninhas: " + e.getMessage());
        }

        log.info("Processamento de ervas daninhas concluído");
    }

    public Area updateAreaWithGeoJson(Long id, AreaGeoJsonDto areaDto) {
        log.info("Atualizando área com ID {} com GeoJSON", id);
        Area area = getAreaOrThrow(id);
        log.debug("Área encontrada para atualização: {}", area);

        area.setNome(areaDto.getNome());
        area.setEstado(areaDto.getEstado());
        area.setCidade(areaDto.getCidade());

        if (areaDto.getGeojson() != null) {
            log.info("Atualizando talhões principais");
            area.clearTalhoes();
            List<Talhao> novosTalhoes = processarGeoJson(areaDto.getGeojson());
            novosTalhoes.forEach(area::addTalhao);
            log.debug("{} talhões atualizados", novosTalhoes.size());
        }

        if (areaDto.getErvasDaninhasGeojson() != null) {
            log.info("Atualizando ervas daninhas");
            processarErvasDaninhasGeoJson(area, areaDto.getErvasDaninhasGeojson());
        }

        if (areaDto.getProdutividadePorAno() != null) {
            log.info("Atualizando produtividade");
            areaDto.getProdutividadePorAno().forEach((mnTl, produtividade) -> {
                area.getTalhoes().stream()
                        .filter(t -> mnTl.equals(t.getMnTl().toString()))
                        .findFirst()
                        .ifPresent(t -> {
                            log.debug("Atualizando produtividade para talhão {}: {}", mnTl, produtividade);
                            t.setProdutividadePorAno(produtividade);
                        });
            });
        }

        log.info("Salvando área atualizada");
        Area updatedArea = areaRepository.save(area);
        log.info("Área ID {} atualizada com sucesso", id);
        return updatedArea;
    }

    public Optional<Area> findAreaById(Long id) {
        log.info("Buscando área por ID: {}", id);
        Optional<Area> area = areaRepository.findById(id);
        if (area.isPresent()) {
            log.debug("Área encontrada: {}", area.get());
        } else {
            log.warn("Área com ID {} não encontrada", id);
        }
        return area;
    }

    public List<Area> findAllAreas() {
        log.info("Buscando todas as áreas");
        List<Area> areas = areaRepository.findAll();
        log.debug("Número de áreas encontradas: {}", areas.size());
        return areas;
    }

    public Area updateArea(Long id, AreaDto areaDto) {
        log.info("Atualizando área básica com ID: {}", id);
        Area area = getAreaOrThrow(id);

        area.setNome(areaDto.getNome());
        area.setEstado(areaDto.getEstado());
        area.setCidade(areaDto.getCidade());

        if (areaDto.getStatus() != null) {
            log.debug("Atualizando status para: {}", areaDto.getStatus());
            area.setStatus(areaDto.getStatus());
        }

        log.info("Salvando área atualizada");
        Area updatedArea = areaRepository.save(area);
        log.info("Área ID {} atualizada com sucesso", id);
        return updatedArea;
    }

    public void deleteArea(Long id) {
        log.info("Deletando área com ID: {}", id);
        if (!areaRepository.existsById(id)) {
            log.error("Área com ID {} não encontrada para deletar", id);
            throw new MessageException("Área não encontrada");
        }
        areaRepository.deleteById(id);
        log.info("Área ID {} deletada com sucesso", id);
    }

    public Talhao updateTalhao(Long areaId, Long talhaoId, TalhaoDto talhaoDto) {
        log.info("Atualizando talhão ID {} da área ID {}", talhaoId, areaId);
        Area area = getAreaOrThrow(areaId);

        Talhao talhao = area.getTalhoes().stream()
                .filter(t -> t.getId().equals(talhaoId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Talhão ID {} não encontrado na área ID {}", talhaoId, areaId);
                    return new MessageException("Talhão não encontrado");
                });

        talhao.setMnTl(talhaoDto.getMnTl());
        talhao.setAreaHaTl(talhaoDto.getAreaHaTl());
        talhao.setSolo(talhaoDto.getSolo());
        talhao.setCultura(talhaoDto.getCultura());
        talhao.setSafra(talhaoDto.getSafra());
        talhao.setProdutividadePorAno(talhaoDto.getProdutividadePorAno());
        talhao.setGeojson(talhaoDto.getGeojson());

        if (talhaoDto.getErvasDaninhas() != null) {
            log.debug("Atualizando ervas daninhas: {}", talhaoDto.getErvasDaninhas());
            talhao.setErvasDaninhas(talhaoDto.getErvasDaninhas());
        }

        log.info("Salvando talhão atualizado");
        Area updatedArea = areaRepository.save(area);

        Talhao updatedTalhao = updatedArea.getTalhoes().stream()
                .filter(t -> t.getId().equals(talhaoId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Erro ao recuperar talhão atualizado");
                    return new MessageException("Erro ao atualizar talhão");
                });

        log.debug("Talhão atualizado com sucesso: {}", updatedTalhao);
        return updatedTalhao;
    }

    public void deleteTalhao(Long areaId, Long talhaoId) {
        log.info("Deletando talhão ID {} da área ID {}", talhaoId, areaId);
        Area area = getAreaOrThrow(areaId);

        boolean removed = area.getTalhoes().removeIf(t -> t.getId().equals(talhaoId));
        if (!removed) {
            log.error("Talhão ID {} não encontrado para deletar", talhaoId);
            throw new MessageException("Talhão não encontrado");
        }

        log.info("Talhão removido - salvando área");
        areaRepository.save(area);
        log.info("Talhão ID {} deletado com sucesso", talhaoId);
    }

    public Map<String, Object> verificarProgresso(Long id) {
        log.info("Verificando progresso da área ID {}", id);
        Area area = getAreaOrThrow(id);

        Map<String, Object> progresso = new HashMap<>();
        progresso.put("etapa1Completa", area.getNome() != null && !area.getNome().isEmpty());
        progresso.put("etapa2Completa", !area.getTalhoes().isEmpty());

        log.debug("Progresso da área ID {}: {}", id, progresso);
        return progresso;
    }

    public Area updateFarmStatus(Long id, String status) {
        log.info("Atualizando status da área ID {} para {}", id, status);
        Area area = getAreaOrThrow(id);

        StatusArea statusEnum = StatusArea.valueOf(status.toUpperCase());
        area.setStatus(statusEnum);

        log.info("Salvando novo status");
        Area updatedArea = areaRepository.save(area);
        log.info("Status da área ID {} atualizado para {}", id, status);
        return updatedArea;
    }

    public Talhao createTalhao(Long areaId, TalhaoDto talhaoDto) {
        log.info("Criando novo talhão para área ID {}", areaId);
        Area area = getAreaOrThrow(areaId);

        Talhao talhao = new Talhao();
        talhao.setMnTl(talhaoDto.getMnTl());
        talhao.setAreaHaTl(talhaoDto.getAreaHaTl());
        talhao.setSolo(talhaoDto.getSolo());
        talhao.setCultura(talhaoDto.getCultura());
        talhao.setSafra(talhaoDto.getSafra());
        talhao.setProdutividadePorAno(talhaoDto.getProdutividadePorAno());
        talhao.setGeojson(talhaoDto.getGeojson());

        if (talhaoDto.getErvasDaninhas() != null) {
            log.debug("Adicionando ervas daninhas: {}", talhaoDto.getErvasDaninhas());
            talhao.setErvasDaninhas(talhaoDto.getErvasDaninhas());
        }

        area.addTalhao(talhao);

        Area savedArea = areaRepository.save(area);

        Talhao savedTalhao = savedArea.getTalhoes().stream()
                .filter(t -> t.getMnTl().equals(talhaoDto.getMnTl()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Erro ao recuperar talhão criado");
                    return new MessageException("Erro ao criar talhão");
                });

        return savedTalhao;
    }

    private Area getAreaOrThrow(Long id) {
        log.debug("Buscando área por ID: {}", id);
        return areaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Área com ID {} não encontrada", id);
                    return new MessageException("Área não encontrada");
                });
    }

    private Integer getIntegerProperty(Map<String, Object> properties, String primaryKey, String secondaryKey) {
        Object value = properties.getOrDefault(primaryKey, properties.get(secondaryKey));
        if (value == null) {
            log.debug("Propriedade inteira não encontrada: {} ou {}", primaryKey, secondaryKey);
            return null;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.error("Erro ao converter propriedade para inteiro: {}", value);
            return null;
        }
    }

    private Double getDoubleProperty(Map<String, Object> properties, String primaryKey, String secondaryKey) {
        Object value = properties.getOrDefault(primaryKey, properties.get(secondaryKey));
        if (value == null) {
            log.debug("Propriedade double não encontrada: {} ou {}", primaryKey, secondaryKey);
            return null;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.error("Erro ao converter propriedade para double: {}", value);
            return null;
        }
    }

    private String getStringProperty(Map<String, Object> properties, String primaryKey, String secondaryKey) {
        Object value = properties.getOrDefault(primaryKey, properties.get(secondaryKey));
        if (value == null) {
            log.debug("Propriedade string não encontrada: {} ou {}", primaryKey, secondaryKey);
            return null;
        }
        return value.toString();
    }

    public void adicionarErvasDaninhas(Long areaId, Long talhaoId, Map<String, Object> geojsonErvas) {
        log.info("Adicionando ervas daninhas ao talhão ID {} da área ID {}", talhaoId, areaId);
        Area area = getAreaOrThrow(areaId);

        Talhao talhao = area.getTalhoes().stream()
                .filter(t -> t.getId().equals(talhaoId))
                .findFirst()
                .orElseThrow(() -> new MessageException("Talhão não encontrado"));

        List<String> ervasExtraidas = extrairErvasDaninhasDeGeoJson(geojsonErvas, talhao.getMnTl());

        if (ervasExtraidas.isEmpty()) {
            log.warn("Nenhuma erva daninha do talhão {} encontrada no GeoJSON", talhao.getMnTl());
        }

        talhao.getErvasDaninhas().addAll(ervasExtraidas);
        areaRepository.save(area);
        log.info("Ervas daninhas adicionadas com sucesso ao talhão {}", talhao.getMnTl());
    }
    @SuppressWarnings("unchecked")
    private List<String> extrairErvasDaninhasDeGeoJson(Map<String, Object> geojson, Integer mnTl) {
        List<String> ervas = new ArrayList<>();

        try {
            List<Map<String, Object>> features = (List<Map<String, Object>>) geojson.get("features");

            for (Map<String, Object> feature : features) {
                Map<String, Object> properties = (Map<String, Object>) feature.get("properties");

                if (properties == null) continue;

                Integer nmTl = getIntegerProperty(properties, "NM_TL", "nmTl");
                String classe = getStringProperty(properties, "CLASSE", "classe");

                if (Objects.equals(nmTl, mnTl) && "DANINHA".equalsIgnoreCase(classe)) {
                    ervas.add(feature.toString()); // armazena a feature completa como string
                }
            }

        } catch (Exception e) {
            log.error("Erro ao extrair ervas daninhas do GeoJSON", e);
            throw new MessageException("Erro ao extrair ervas daninhas do GeoJSON: " + e.getMessage());
        }

        return ervas;
    }
    public void removerErvasDaninhas(Long areaId, Long talhaoId) {
        log.info("Removendo ervas daninhas do talhão ID {} da área ID {}", talhaoId, areaId);
        Area area = getAreaOrThrow(areaId);

        Talhao talhao = area.getTalhoes().stream()
                .filter(t -> t.getId().equals(talhaoId))
                .findFirst()
                .orElseThrow(() -> new MessageException("Talhão não encontrado"));

        talhao.getErvasDaninhas().clear();
        areaRepository.save(area);
        log.info("Todas as ervas daninhas removidas do talhão {}", talhao.getMnTl());
    }
}
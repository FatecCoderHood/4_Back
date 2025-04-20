package coderhood.repository;

import coderhood.model.Talhao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TalhaoRepository extends JpaRepository<Talhao, UUID> {
    List<Talhao> findByAreaId(UUID areaId);
}

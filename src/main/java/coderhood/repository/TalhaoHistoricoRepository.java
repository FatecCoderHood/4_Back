package coderhood.repository;

import coderhood.model.TalhaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalhaoHistoricoRepository extends JpaRepository<TalhaoHistorico, Long> {
    List<TalhaoHistorico> findByTalhaoIdOrderByDataAlteracaoDesc(Long talhaoId);
}

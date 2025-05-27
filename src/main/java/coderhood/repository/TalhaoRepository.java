package coderhood.repository;

import coderhood.model.Talhao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TalhaoRepository extends JpaRepository<Talhao, Long> {
    
    @Query("SELECT COUNT(t) FROM Talhao t WHERE t.analistaId = :analistaId")
    Long countByAnalistaId(@Param("analistaId") Long analistaId);
}

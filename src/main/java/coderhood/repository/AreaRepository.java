package coderhood.repository;

import coderhood.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {  // Alterado de UUID para Long
}
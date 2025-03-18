package coderhood.repository;

import coderhood.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {
    Optional<Area> buscarAreaPorId(UUID id);
}

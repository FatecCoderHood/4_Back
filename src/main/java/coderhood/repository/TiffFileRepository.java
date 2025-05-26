package coderhood.repository;

import coderhood.model.TiffFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TiffFileRepository extends JpaRepository<TiffFile, UUID> {
    List<TiffFile> findByAreaId(Long areaId); // Alterado para Long
}
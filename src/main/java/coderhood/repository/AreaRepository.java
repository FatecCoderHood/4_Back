package coderhood.repository;

import coderhood.dto.area.AreaBasicDto;
import coderhood.model.Area;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AreaRepository extends JpaRepository<Area, Long>
{
    @Query("SELECT new coderhood.dto.area.AreaBasicDto(a.id, a.nome, a.estado, a.cidade, a.status) FROM Area a")
    List<AreaBasicDto> findAllBasicDto();
}
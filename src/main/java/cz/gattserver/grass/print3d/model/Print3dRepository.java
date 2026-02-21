package cz.gattserver.grass.print3d.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Print3dRepository extends JpaRepository<Print3d, Long> {

	@Query(value = "select p.projectDir from PRINT3D p where p.id = ?1")
	String findProjectDirById(Long id);

}
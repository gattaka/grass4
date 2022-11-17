package cz.gattserver.grass.print3d.model.repositories;

import cz.gattserver.grass.print3d.model.domain.Print3d;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Print3dRepository extends JpaRepository<Print3d, Long> {

	@Query(value = "select count(p) from PRINT3D p where (p.contentNode.publicated = true or p.contentNode.author.id = ?1) and lower(p.contentNode.name) like lower(?2)")
	int countByUserAccess(Long userId, String filter);

	@Query(value = "select p.projectPath from PRINT3D p where p.id = ?1")
	String findProjectPathById(Long id);

}

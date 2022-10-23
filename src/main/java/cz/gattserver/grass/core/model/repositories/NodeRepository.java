package cz.gattserver.grass.core.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.core.model.domain.Node;

public interface NodeRepository extends JpaRepository<Node, Long> {

	List<Node> findByParentIsNull();

	int countByParentIsNull();

	List<Node> findByParentId(Long id);

	int countByParentId(Long id);

	@Modifying
	@Query("update NODE n set n.name = ?2 where n.id = ?1")
	void rename(Long nodeId, String newName);

	@Query("select count(c) from NODE n join CONTENTNODE c on c.parent.id = n.id where n.id = ?1")
	int countSubNodes(Long nodeId);

	@Query("select count(s) from NODE n join NODE s on s.parent.id = n.id where n.id = ?1")
	int countContentNodes(Long nodeId);

}

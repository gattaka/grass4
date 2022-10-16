package cz.gattserver.grass.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.model.domain.ContentNode;

public interface ContentNodeRepository extends JpaRepository<ContentNode, Long>, ContentNodeRepositoryCustom {

	@Query("select c.id from CONTENTNODE c where c.contentReaderId = ?1 and c.contentId = ?2")
	Long findIdByContentModuleAndContentId(String contentModuleId, Long contentId);

	@Modifying
	@Query("update CONTENTNODE c set c.parent.id = ?1 where c.id = ?2")
	void moveContent(Long nodeId, Long contentNodeId);
}

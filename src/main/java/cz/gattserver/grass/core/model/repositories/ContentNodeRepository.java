package cz.gattserver.grass.core.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.core.model.domain.ContentNode;

public interface ContentNodeRepository extends JpaRepository<ContentNode, Long>, ContentNodeRepositoryCustom {

	@Query("select c.id from CONTENT_NODE c where c.contentReaderId = ?1 and c.contentId = ?2")
	Long findIdByContentModuleAndContentId(String contentModuleId, Long contentId);

	@Modifying
	@Query("update CONTENT_NODE c set c.parentId = ?2, c.hiddenByParent = ?3 where c.id = ?1")
	void moveContent(Long contentNodeId, Long nodeId , boolean hiddenByParent);

    @Modifying
    @Query("update CONTENT_NODE c set c.hiddenByParent = ?2 where c.parentId = ?1")
    void updateHiddenByParentByNode(Long nodeId, boolean hiddenByParent);
}

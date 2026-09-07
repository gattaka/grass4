package cz.gattserver.grass.core.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.core.model.domain.ContentNode;

public interface ContentNodeRepository extends JpaRepository<ContentNode, Long>, ContentNodeRepositoryCustom {

	@Query("select c.id from CONTENT_NODE c where c.contentReaderId = ?1 and c.contentId = ?2")
	Long findIdByContentModuleAndContentId(String contentModuleId, Long contentId);

	@Modifying
	@Query("update CONTENT_NODE c set c.parentId = ?1 where c.id = ?2")
	void moveContent(Long nodeId, Long contentNodeId);

    @Modifying
    @Query("update CONTENT_NODE c set c.hiddenByParent = ?2 where c.parentId = ?1")
    void updateHiddenByParentByNode(Long nodeId, boolean hiddenByParent);
}

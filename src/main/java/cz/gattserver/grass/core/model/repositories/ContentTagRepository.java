package cz.gattserver.grass.core.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.core.model.domain.ContentTag;

public interface ContentTagRepository extends JpaRepository<ContentTag, Long>, ContentTagRepositoryCustom {

	ContentTag findByName(String name);

	@Modifying
	@Query("delete CONTENT_TAG c where c.contentNodeCount = 0")
	void deleteUnusedTags();

	@Query(value = "select id, COUNT(contentnodes_id) as c from CONTENT_TAG join CONTENTNODE_CONTENT_TAG on CONTENT_TAG.id = contenttags_id group by id order by c",
			nativeQuery = true)
	List<Object[]> countContentTagsContents();

	@Query("select t from CONTENT_TAG t order by UPPER(t.name)")
	List<ContentTag> findAllOrderByNameCaseInsensitive();

}
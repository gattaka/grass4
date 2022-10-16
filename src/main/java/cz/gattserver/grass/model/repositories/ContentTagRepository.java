package cz.gattserver.grass.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.model.domain.ContentTag;

public interface ContentTagRepository extends JpaRepository<ContentTag, Long>, ContentTagRepositoryCustom {

	ContentTag findByName(String name);

	@Modifying
	@Query("delete CONTENT_TAG c where size(c.contentNodes) = '0'")
	void deleteUnusedTags();

	@Query(value = "select c from (select count(contenttags_id) as c from CONTENTNODE_CONTENT_TAG group by contenttags_id ) as counts group by c order by c asc;",
			nativeQuery = true)
	List<Object> findContentNodesCountsGroups();

	@Query(value = "select id, COUNT(contentnodes_id) as c from CONTENT_TAG join CONTENTNODE_CONTENT_TAG on CONTENT_TAG.id = contenttags_id group by id order by c",
			nativeQuery = true)
	List<Object[]> countContentTagsContents();

	@Query("select t from CONTENT_TAG t order by UPPER(t.name)")
	List<ContentTag> findAllOrderByNameCaseInsensitive();

}

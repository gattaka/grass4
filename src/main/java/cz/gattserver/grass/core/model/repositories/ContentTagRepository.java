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

}
package cz.gattserver.grass.language.model.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.domain.LanguageItem;

public interface LanguageItemRepository extends JpaRepository<LanguageItem, Long>, LanguageItemRepositoryCustom {

	@Query("select i.id from LANGUAGEITEM i where i.language.id = ?1 and i.type = ?2 and i.successRate >= ?3 and i.successRate < ?4 order by content asc")
	List<Long> findIdsByLanguageAndSuccessRateRangeSortByContent(Long languageId, ItemType type, double minRate,
			double maxRate);

	@Query("select i.id from LANGUAGEITEM i where i.language.id = ?1 and i.successRate >= ?2 and i.successRate < ?3 order by content asc")
	List<Long> findIdsByLanguageAndSuccessRateRangeSortByContent(Long languageId, double minRate, double maxRate);

	@Query("select i from LANGUAGEITEM i where i.id in ?1")
	List<LanguageItem> findByIds(Set<Long> ids);

	@Query("select i from LANGUAGEITEM i where i.language.id = ?1 and i.content = ?2")
	LanguageItem findLanguageItemByContent(Long languageId, String content);

	@Modifying
	@Query("update LANGUAGEITEM i set i.tested = ?2, i.successRate = ?3, i.lastTested = ?4 where i.id = ?1")
	void updateItem(Long id, Integer newCount, Double newRate, LocalDateTime now);

	@Query("select i from LANGUAGEITEM i where i.language.id = ?1 and i.type = ?2 and LENGTH(i.content) < ?3")
	List<LanguageItem> findItemOfMaxLength(Long languageId, ItemType type, Integer i);

	@Query("select SUM(i.successRate) from LANGUAGEITEM i where i.language.id = ?2 and i.type = ?1")
	Integer findSuccessRateSumByLanguageAndType(ItemType type, Long langId);

	@Query("select count(i) from LANGUAGEITEM i where i.language.id = ?2 and i.type = ?1")
	Integer countByLanguageAndType(ItemType type, Long langId);

	@Modifying
	@Query("update LANGUAGEITEM i set i.language.id = ?2 where i.id = ?1")
	void updateItemLang(Long id, Long id2);

	@Query(value = "select successrate, count(*) from LANGUAGEITEM where language_id = ?1 group by successrate order by successrate desc",
			nativeQuery = true)
	List<Object[]> findStatisticsByLanguage(Long languageId);

	@Query(value = "select successrate, count(*) from LANGUAGEITEM where type = ?1 and language_id = ?2 group by successrate order by successrate desc",
			nativeQuery = true)
	List<Object[]> findStatisticsByLanguage(Integer type, Long languageId);

}

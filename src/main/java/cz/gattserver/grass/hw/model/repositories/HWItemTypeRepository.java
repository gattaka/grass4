package cz.gattserver.grass.hw.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.hw.model.domain.HWItemType;

public interface HWItemTypeRepository extends JpaRepository<HWItemType, Long>, HWItemTypeRepositoryCustom {

	@Query("select t from HW_ITEM_TYPE t order by name asc")
	List<HWItemType> findListOrderByName();

	@Query("select t from HW_ITEM_TYPE t where t.name = ?1")
	HWItemType findByName(String name);

	@Modifying
	@Query(value = "delete HW_ITEM_TYPE where not exists (select 1 from HW_ITEM_HW_ITEM_TYPE where TYPES_ID = HW_ITEM_TYPE.ID)",
			nativeQuery = true)
	void cleanOrphansName();
}

package cz.gattserver.grass.hw.model.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.hw.model.domain.HWItem;

public interface HWItemRepository extends JpaRepository<HWItem, Long>, HWItemRepositoryCustom {

	List<HWItem> findByTypesId(Long id);

	List<HWItem> findByUsedInId(Long id);

	@Query("select i from HW_ITEM i inner join i.types types where types.name in ?1")
	List<HWItem> getHWItemsByTypes(Collection<String> types);

	@Query("select i from HW_ITEM i where i.id <> ?1")
	List<HWItem> findAllExcept(Long itemId);

	@Query("select i.id from HW_ITEM i")
	List<Long> findAllIds();
}

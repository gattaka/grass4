package cz.gattserver.grass.hw.model.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.hw.model.domain.HWItem;

public interface HWItemRepository extends JpaRepository<HWItem, Long>, HWItemRepositoryCustom {

	public List<HWItem> findByTypesId(Long id);

	public List<HWItem> findByUsedInId(Long id);

	@Query("select i from HW_ITEM i inner join i.types types where types.name in ?1")
	public List<HWItem> getHWItemsByTypes(Collection<String> types);

	@Query("select i from HW_ITEM i where i.id <> ?1")
	public List<HWItem> findAllExcept(Long itemId);
}

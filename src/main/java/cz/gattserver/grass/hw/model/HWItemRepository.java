package cz.gattserver.grass.hw.model;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass.hw.interfaces.HWItemState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HWItemRepository extends JpaRepository<HWItem, Long>, HWItemRepositoryCustom {

	@Query("select i.id from HW_ITEM i")
	List<Long> findAllIds();

    @Modifying
    @Query("update HW_ITEM set state = ?2 where id = ?1")
    void updateState(Long id, HWItemState state);

    @Modifying
    @Query("update HW_ITEM set usedInId = ?2 where id = ?1")
    void updateUsedInId(Long id, Long targetItemId);
}

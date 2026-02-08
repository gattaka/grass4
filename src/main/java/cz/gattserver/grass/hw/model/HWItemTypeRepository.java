package cz.gattserver.grass.hw.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface HWItemTypeRepository extends JpaRepository<HWItemType, HWItemTypeId> {

    @Modifying
    @Query("delete HW_ITEM_TYPE where id.hwItemId = ?1 and id.hwTypeId in ?2")
    void deleteItemType(Long hwItemId, Set<Long> hwTypeIdSet);

}
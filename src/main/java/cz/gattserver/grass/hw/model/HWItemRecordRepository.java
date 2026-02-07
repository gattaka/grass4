package cz.gattserver.grass.hw.model;

import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HWItemRecordRepository extends JpaRepository<HWItemRecord, Long>, HWItemRecordRepositoryCustom {

    @Modifying
    @Query("delete HW_ITEM_RECORD where hwItemId = ?1")
    void deleteByItemId(Long id);

}

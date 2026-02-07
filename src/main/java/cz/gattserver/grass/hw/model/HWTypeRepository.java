package cz.gattserver.grass.hw.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HWTypeRepository extends JpaRepository<HWType, Long>, HWTypeRepositoryCustom {

}

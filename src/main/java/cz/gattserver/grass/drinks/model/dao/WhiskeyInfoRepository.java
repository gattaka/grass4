package cz.gattserver.grass.drinks.model.dao;

import cz.gattserver.grass.drinks.model.domain.WhiskeyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhiskeyInfoRepository extends JpaRepository<WhiskeyInfo, Long> {

}

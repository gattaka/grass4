package cz.gattserver.grass.drinks.model.dao;

import cz.gattserver.grass.drinks.model.domain.WineInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineInfoRepository extends JpaRepository<WineInfo, Long> {

}

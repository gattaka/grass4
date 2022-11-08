package cz.gattserver.grass.drinks.model.dao;

import cz.gattserver.grass.drinks.model.domain.BeerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerInfoRepository extends JpaRepository<BeerInfo, Long> {

}

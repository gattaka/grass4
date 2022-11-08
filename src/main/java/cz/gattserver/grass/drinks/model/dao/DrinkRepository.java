package cz.gattserver.grass.drinks.model.dao;

import cz.gattserver.grass.drinks.model.domain.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrinkRepository extends JpaRepository<Drink, Long>, DrinkRepositoryCustom {

}

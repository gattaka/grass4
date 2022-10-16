package cz.gattserver.grass.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.model.domain.ConfigurationItem;

public interface ConfigurationItemRepository extends JpaRepository<ConfigurationItem, String> {

	List<ConfigurationItem> findByNameStartingWith(String prefix);

}

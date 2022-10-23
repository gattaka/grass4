package cz.gattserver.grass.core.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.core.model.domain.ConfigurationItem;

public interface ConfigurationItemRepository extends JpaRepository<ConfigurationItem, String> {

	List<ConfigurationItem> findByNameStartingWith(String prefix);

}

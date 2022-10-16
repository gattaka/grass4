package cz.gattserver.grass.services.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.config.AbstractConfiguration;
import cz.gattserver.grass.NonConfigValue;
import cz.gattserver.common.util.StringSerializer;
import cz.gattserver.grass.model.domain.ConfigurationItem;
import cz.gattserver.grass.model.repositories.ConfigurationItemRepository;
import cz.gattserver.grass.services.ConfigurationService;

@Transactional
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	private Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	@Autowired
	private ConfigurationItemRepository configurationItemRepository;

	public void loadConfiguration(AbstractConfiguration configuration) {
		Validate.notNull(configuration, "'configuration' nemůže být null");
		List<ConfigurationItem> configurationItems = configurationItemRepository
				.findByNameStartingWith(configuration.getPrefix());
		populateConfigurationFromMap(configurationItems, configuration);
	}

	public void saveConfiguration(AbstractConfiguration configuration) {
		Validate.notNull(configuration, "'configuration' nemůže být null");
		List<ConfigurationItem> items = getConfigurationItems(configuration);
		// protože ukládá i null hodnoty jako novou configItem, uloží buď
		// všechno nebo nic
		for (ConfigurationItem item : items)
			configurationItemRepository.save(item);
	}

	private String createConfigName(AbstractConfiguration configuration, String name) {
		return configuration.getPrefix() + "." + name;
	}

	private String createMethodName(String prefix, String fieldName) {
		return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	private String createGetMethodName(String fieldName) {
		return createMethodName("get", fieldName);
	}

	private String createIsMethodName(String fieldName) {
		return createMethodName("is", fieldName);
	}

	private String createSetMethodName(String fieldName) {
		return createMethodName("set", fieldName);
	}

	private Method getGetMethod(Field field, Class<? extends AbstractConfiguration> type) {
		Class<?>[] params = {};
		try {
			return type.getDeclaredMethod(createGetMethodName(field.getName()), params);
		} catch (Exception e) {
			return null;
		}
	}

	private Method getIsMethod(Field field, Class<? extends AbstractConfiguration> type) {
		Class<?>[] params = {};
		try {
			return type.getDeclaredMethod(createIsMethodName(field.getName()), params);
		} catch (Exception e) {
			return null;
		}
	}

	public <T extends AbstractConfiguration> List<ConfigurationItem> getConfigurationItems(T configuration) {
		List<ConfigurationItem> items = new ArrayList<>();
		Class<? extends AbstractConfiguration> type = configuration.getClass();
		for (Field field : type.getDeclaredFields()) {
			NonConfigValue annotation = field.getAnnotation(NonConfigValue.class);
			if (annotation == null) {
				String value = null;
				Object[] args = {};
				Method getMethod = null;
				getMethod = getGetMethod(field, type);
				if (getMethod == null)
					getMethod = getIsMethod(field, type);
				if (getMethod == null) {
					logger.error("Nezdařilo se získat pole konfigurační položky {}", field.getName());
					continue;
				}
				try {
					value = StringSerializer.serialize((Serializable) getMethod.invoke(configuration, args));
				} catch (Exception e) {
					logger.error("Nezdařilo se získat hodnotu konfigurační položky {}", field.getName(), e);
				}
				items.add(new ConfigurationItem(createConfigName(configuration, field.getName()), value));
			}
		}
		return items;

	}

	public <T extends AbstractConfiguration> void populateConfigurationFromMap(List<ConfigurationItem> items,
			T configuration) {
		Class<? extends AbstractConfiguration> type = configuration.getClass();
		for (Field field : type.getDeclaredFields()) {
			NonConfigValue annotation = field.getAnnotation(NonConfigValue.class);
			if (annotation != null)
				continue;

			for (ConfigurationItem item : items) {
				int subindex = (configuration.getPrefix().length() + 1) >= item.getName().length() ? 0
						: configuration.getPrefix().length() + 1;
				if (field.getName().equals(item.getName().substring(subindex))) {
					try {
						Object[] args = { StringSerializer.deserialize(item.getValue()) };
						Class<?>[] params = { field.getType() };
						Method setMethod = null;
						setMethod = type.getDeclaredMethod(createSetMethodName(field.getName()), params);
						setMethod.invoke(configuration, args);
					} catch (Exception e) {
						logger.error("Deserializace nastavení " + configuration.getClass()
								+ ": Nezdařilo se nastavit hodnotu konfigurační položky {}", item.getName(), e);
						continue;
					}
				}
			}
		}
	}

}

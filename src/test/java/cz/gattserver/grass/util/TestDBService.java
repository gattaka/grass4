package cz.gattserver.grass.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://objectpartners.com/2021/07/14/resetting-database-between-spring-integration-tests/
 */
@Service
public class TestDBService {

    @Autowired
    private EntityManager entityManager;

    private List<String> tableNames;

    @PostConstruct
    void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .map(entityType -> entityType.getJavaType().getAnnotation(Entity.class))
                .map(this::convertToTableName)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public void resetDatabase() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames)
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String convertToTableName(Entity table) {
        String tableName = table.name();
        return tableName.replaceAll("([a-z])([A-Z])", "$1_$2");
    }
}
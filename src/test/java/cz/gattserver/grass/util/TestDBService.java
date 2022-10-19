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

    @Transactional
    public void resetDatabase() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        List<Object[]> results = entityManager.createNativeQuery("show tables").getResultList();
        for (Object[] result : results)
            entityManager.createNativeQuery("TRUNCATE TABLE " + result[0]).executeUpdate();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String convertToTableName(Entity table) {
        String tableName = table.name();
        return tableName.replaceAll("([a-z])([A-Z])", "$1_$2");
    }
}
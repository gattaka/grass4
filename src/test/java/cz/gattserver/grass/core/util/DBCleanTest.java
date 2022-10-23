package cz.gattserver.grass.core.util;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DBCleanTest {

    @Autowired
    private TestDBService testDbService;

    @AfterEach
    void afterEach() {
        testDbService.resetDatabase();
    }

}

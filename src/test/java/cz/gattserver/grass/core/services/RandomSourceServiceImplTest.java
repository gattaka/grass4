package cz.gattserver.grass.core.services;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.core.mock.MockRandomSourceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RandomSourceServiceImplTest {

    @Autowired
    private RandomSourceService randomSourceService;

    @Test
    public void test() {
        MockRandomSourceImpl.longValuesIndex = 0;
        MockRandomSourceImpl.longValues = new long[]{0L, 5L};
        assertEquals(0, randomSourceService.getRandomLong(0L));
        assertEquals(5L, randomSourceService.getRandomLong(2L));
        assertEquals(0, randomSourceService.getRandomLong(0L));

        MockRandomSourceImpl.intValuesIndex = 0;
        MockRandomSourceImpl.intValues = new int[]{3, 20, 1};
        assertEquals(3, randomSourceService.getRandomInt(0));
        assertEquals(20, randomSourceService.getRandomInt(2));
        assertEquals(1, randomSourceService.getRandomInt(5));
        assertEquals(3, randomSourceService.getRandomInt(5));
    }

}

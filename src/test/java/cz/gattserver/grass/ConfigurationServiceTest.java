package cz.gattserver.grass;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.mock.MockConfiguration;
import cz.gattserver.grass.services.ConfigurationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GrassApplication.class)
public class ConfigurationServiceTest {

    @Autowired
    private ConfigurationService configurationService;

    @Test
    public void testSaveConfiguration() {
        MockConfiguration mc = new MockConfiguration("mockConfiguration");
        mc.setConfigValue("mockConfigValue");
        mc.setConfigValue2("mockConfigValue2");
        mc.setNonConfigValue("mockNonConfigValue");
        configurationService.saveConfiguration(mc);

        MockConfiguration toLoadMc = new MockConfiguration("mockConfiguration");
        toLoadMc.setConfigValue("defaultMockConfigValue");
        configurationService.loadConfiguration(toLoadMc);

        assertEquals("mockConfigValue", toLoadMc.getConfigValue());
        assertEquals("mockConfigValue2", toLoadMc.getConfigValue2());
        assertNull(toLoadMc.getNonConfigValue());
    }

    @Test
    public void testSaveConfiguration2() {
        MockConfiguration mc = new MockConfiguration("mockConfiguration");
        mc.setConfigValue("mockConfigValue");
        mc.setConfigValue2("mockConfigValue2");
        mc.setNonConfigValue("mockNonConfigValue");
        configurationService.saveConfiguration(mc);

        MockConfiguration toLoadMc = new MockConfiguration("mockConfiguration");
        toLoadMc.setConfigValue("defaultMockConfigValue");
        configurationService.loadConfiguration(toLoadMc);

        assertEquals("mockConfigValue", toLoadMc.getConfigValue());
        assertEquals("mockConfigValue2", toLoadMc.getConfigValue2());
        assertNull(toLoadMc.getNonConfigValue());

        mc = new MockConfiguration("mockConfiguration");
        mc.setConfigValue("mockConfigValueChanged");
        configurationService.saveConfiguration(mc);

        toLoadMc = new MockConfiguration("mockConfiguration");
        toLoadMc.setConfigValue("defaultMockConfigValue");
        toLoadMc.setConfigValue2("defaultMockConfigValue2");
        configurationService.loadConfiguration(toLoadMc);

        assertEquals("mockConfigValueChanged", toLoadMc.getConfigValue());
        assertNull(toLoadMc.getConfigValue2());
        assertNull(toLoadMc.getNonConfigValue());
    }

    @Test
    public void testSaveConfiguration_fail() {
        assertThrows(NullPointerException.class, () ->
                configurationService.saveConfiguration(null));
    }

    @Test
    public void testLoadConfiguration() {
        MockConfiguration toLoadMc = new MockConfiguration("mockConfiguration");
        toLoadMc.setConfigValue("defaultMockConfigValue");
        configurationService.loadConfiguration(toLoadMc);

        assertEquals("defaultMockConfigValue", toLoadMc.getConfigValue());
        assertNull(toLoadMc.getNonConfigValue());
    }

    @Test
    public void testLoadConfiguration_fail() {
        assertThrows(NullPointerException.class, () -> configurationService.loadConfiguration(null));
    }

}

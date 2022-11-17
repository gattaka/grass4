package cz.gattserver.grass.fm.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.fm.web.factories.FMPageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class FMPageFactoryTest extends DBCleanTest {

    @Autowired
    @Qualifier("fmPageFactory")
    private PageFactory pageFactory;

    @Test
    public void testFMPageFactory() {
        assertTrue(pageFactory instanceof FMPageFactory);
        FMPageFactory factory = (FMPageFactory) pageFactory;

        assertEquals("fm", factory.getPageName());
    }

}

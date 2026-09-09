package cz.gattserver.grass.pg.ui.pages.factories;

import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.pg.ui.pages.PGSettingsPageFragmentFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.stereotype.Component;

@Component
public class PGSettingsPageFactory extends AbstractModuleSettingsPageFactory {

    private final PGService pgService;
    private final EventBus eventBus;
    private final FileSystemService fileSystemService;

    public PGSettingsPageFactory(PGService pgService, EventBus eventBus, FileSystemService fileSystemService) {
        super("Fotogalerie", "photogallery");
        this.pgService = pgService;
        this.eventBus = eventBus;
        this.fileSystemService = fileSystemService;
    }

    public boolean isAuthorized() {
        return getUser().isAdmin();
    }

    @Override
    protected AbstractPageFragmentFactory createPageFragmentFactory() {
        return new PGSettingsPageFragmentFactory(pgService, eventBus, fileSystemService);
    }
}
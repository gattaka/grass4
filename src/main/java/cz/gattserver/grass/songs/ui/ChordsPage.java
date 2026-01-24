package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.util.ChordImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("chords")
@PageTitle("Akordy")
public class ChordsPage extends OneColumnPage implements HasUrlParameter<String> {

    private static final long serialVersionUID = -6336711256361320029L;

    private static final Logger logger = LoggerFactory.getLogger(ChordsPage.class);

    @Autowired
    private SongsService songsService;

    private TabsMenu tabsMenu;

    private Grid<ChordTO> grid;
    private H2 nameLabel;
    private VerticalLayout chordDescriptionLayout;

    private ChordTO choosenChord;
    private List<ChordTO> chords;
    private Map<ChordTO, Integer> indexMap = new HashMap<>();
    private ChordTO filterTO;

    private String chordName;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) chordName = parameter;

        if (tabsMenu == null) init();

        if (chordName != null) {
            ChordTO to = songsService.getChordByName(chordName);
            selectChord(to);
        } else {
            selectChord(null);
        }
    }

    @Override
    protected void createColumnContent(Div layout) {
        tabsMenu = new TabsMenu();
        layout.add(tabsMenu);
        tabsMenu.selectChordsTab();

        chords = new ArrayList<>();
        filterTO = new ChordTO();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(mainLayout);

        grid = new Grid<>();
        grid.setItems(chords);
        Grid.Column<ChordTO> nazevColumn = grid.addColumn(ChordTO::getName).setHeader("Název");
        grid.setWidth("398px");
        grid.setHeight("600px");
        mainLayout.add(grid);
        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(nazevColumn), e -> {
            filterTO.setName(e.getValue());
            loadChords();
        });

        loadChords();

        grid.addSelectionListener((e) -> {
            if (e.getFirstSelectedItem().isPresent()) {
                ChordTO choosenChord = e.getFirstSelectedItem().get();
                showDetail(choosenChord);
                UI.getCurrent().getPage().getHistory().replaceState(null, "chords/" + choosenChord.getName());
            } else {
                showDetail(null);
                UI.getCurrent().getPage().getHistory().replaceState(null, "chords");
            }
        });

        Div panel = new Div();
        panel.setWidth("560px");
        panel.getStyle().set("padding", "10px").set("background", "white").set("border-radius", "3px")
                .set("border", "1px solid #d5d5d5");
        mainLayout.add(panel);

        nameLabel = new H2();
        nameLabel.setVisible(false);
        panel.add(nameLabel);

        chordDescriptionLayout = new VerticalLayout();
        panel.add(chordDescriptionLayout);

        Div btnLayout = componentFactory.createButtonLayout();
        btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(btnLayout);

        btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton("Přidat", event -> new ChordDialog(to -> {
            to = songsService.saveChord(to);
            loadChords();
            selectChord(to);
        }).open()));

        btnLayout.add(componentFactory.createEditGridButton(event -> new ChordDialog(choosenChord, to -> {
            to = songsService.saveChord(to);
            loadChords();
            selectChord(to);
        }).open(), grid));

        btnLayout.add(componentFactory.createCopyGridButton(event -> new ChordDialog(choosenChord, true, to -> {
            to = songsService.saveChord(to);
            loadChords();
            selectChord(to);
        }).open(), grid));

        btnLayout.add(componentFactory.createDeleteGridSetButton(items -> {
            for (ChordTO c : items)
                songsService.deleteChord(c.getId());
            loadChords();
            showDetail(null);
        }, grid));
    }

    public void selectChord(ChordTO choosenChord) {
        if (choosenChord != null) {
            grid.select(choosenChord);
            UIUtils.scrollGridToIndex(grid, indexMap.get(choosenChord));
        }
    }

    private void showDetail(ChordTO choosenChord) {
        chordDescriptionLayout.removeAll();
        if (choosenChord == null) {
            nameLabel.setVisible(false);
            this.choosenChord = null;
        } else {
            nameLabel.setText(choosenChord.getName());
            nameLabel.setVisible(true);
            Span chordDisplayLabel = new Span();
            createDisplay(choosenChord);
            chordDescriptionLayout.add(chordDisplayLabel);
            this.choosenChord = choosenChord;
        }
    }

    private void createDisplay(ChordTO choosenChord) {
        BufferedImage image = ChordImageUtils.drawChord(choosenChord, 30);
        VerticalLayout layout = new VerticalLayout();
        chordDescriptionLayout.add(layout);
        String name = "Chord-" + choosenChord.getName();
        layout.add(new Image(DownloadHandler.fromInputStream(e -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", os);
                return new DownloadResponse(new ByteArrayInputStream(os.toByteArray()), name, null, -1);
            } catch (IOException ex) {
                logger.error("Nezdařilo se vytváření thumbnail akordu", ex);
                return null;
            }
        }), name));
    }

    private void loadChords() {
        chords.clear();
        indexMap.clear();
        chords.addAll(songsService.getChords(filterTO));
        for (int i = 0; i < chords.size(); i++)
            indexMap.put(chords.get(i), i);
        grid.getDataProvider().refreshAll();
    }
}
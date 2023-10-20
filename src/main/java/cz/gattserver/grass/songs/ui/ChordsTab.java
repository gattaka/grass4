package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.CreateGridButton;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.util.ChordImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChordsTab extends Div {

	private static final long serialVersionUID = 2599065817744507785L;

	private static final Logger logger = LoggerFactory.getLogger(ChordsTab.class);

	@Autowired
	private SongsService songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	private Grid<ChordTO> grid;
	private H2 nameLabel;
	private VerticalLayout chordDescriptionLayout;

	private ChordTO choosenChord;
	private List<ChordTO> chords;
	private Map<ChordTO, Integer> indexMap = new HashMap<>();
	private ChordTO filterTO;

	public ChordsTab(SongsPage songsPage) {
		SpringContextHelper.inject(this);

		chords = new ArrayList<>();
		filterTO = new ChordTO();

		HorizontalLayout mainLayout = new HorizontalLayout();
		add(mainLayout);

		grid = new Grid<>();
		grid.setItems(chords);
		Column<ChordTO> nazevColumn = grid.addColumn(ChordTO::getName).setHeader("Název");
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
			if (e.getFirstSelectedItem().isPresent())
				showDetail(e.getFirstSelectedItem().get());
			else
				showDetail(null);
		});

		Div panel = new Div();
		panel.setWidth("560px");
		panel.getStyle().set("padding", "10px").set("background", "white").set("border-radius", "3px").set("border",
				"1px solid #d5d5d5");
		mainLayout.add(panel);

		nameLabel = new H2();
		nameLabel.setVisible(false);
		panel.add(nameLabel);

		chordDescriptionLayout = new VerticalLayout();
		panel.add(chordDescriptionLayout);

		ButtonLayout btnLayout = new ButtonLayout();
		add(btnLayout);

		btnLayout.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		btnLayout.add(new CreateGridButton("Přidat", event -> {
			new ChordDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					loadChords();
					selectChord(to);
				}
			}.open();
		}));

		btnLayout.add(new ModifyGridButton<ChordTO>("Upravit", event -> {
			new ChordDialog(choosenChord) {

				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					loadChords();
					selectChord(to);
				}
			}.open();
		}, grid));

		GridButton<ChordTO> copyBtn = new GridButton<>("Kopie", event -> {
			new ChordDialog(choosenChord, true) {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(ChordTO to) {
					to = songsFacade.saveChord(to);
					loadChords();
					selectChord(to);
				}
			}.open();
		}, grid);
		copyBtn.setIcon(new Image(ImageIcon.QUICKEDIT_16_ICON.createResource(), "Kopie"));
		btnLayout.add(copyBtn);

		btnLayout.add(new DeleteGridButton<ChordTO>("Smazat", items -> {
			for (ChordTO c : items)
				songsFacade.deleteChord(c.getId());
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
		layout.add(new Image(new StreamResource(name, () -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(image, "png", os);
				return new ByteArrayInputStream(os.toByteArray());
			} catch (IOException e) {
				logger.error("Nezdařilo se vytváření thumbnail akordu", e);
				return null;
			}
		}), name));
	}

	private void loadChords() {
		chords.clear();
		indexMap.clear();
		chords.addAll(songsFacade.getChords(filterTO));
		for (int i = 0; i < chords.size(); i++)
			indexMap.put(chords.get(i), i);
		grid.getDataProvider().refreshAll();
	}
}

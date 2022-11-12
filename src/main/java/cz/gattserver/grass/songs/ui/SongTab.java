package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.export.ExportType;
import cz.gattserver.grass.core.export.ExportsService;
import cz.gattserver.grass.core.export.JasperExportDataSource;
import cz.gattserver.grass.core.export.PagedDataSource;
import cz.gattserver.grass.core.server.ExportRequestHandler;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.components.button.ModifyButton;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;
import cz.gattserver.grass.songs.util.ChordImageUtils;
import net.sf.jasperreports.engine.JRDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SongTab extends Div {

	private static final long serialVersionUID = 594189301140808163L;

	private static final Logger logger = LoggerFactory.getLogger(SongTab.class);

	private static final String JS_DIV_ID = "js-div";

	@Autowired
	private SongsService songsFacade;

	@Autowired
	private SecurityService securityService;

	@Resource(name = "songsPageFactory")
	private SongsPageFactory pageFactory;

	@Autowired
	private ExportsService exportsService;

	private H2 nameLabel;
	private HtmlDiv authorYearLabel;
	private HtmlDiv contentLabel;
	private HtmlDiv embeddedLabel;

	private SongTO choosenSong;

	public SongTab(SongsPage songsPage) {
		SpringContextHelper.inject(this);

		Div wrapperDiv = new Div();
		wrapperDiv.getStyle().set("padding", "10px").set("background", "white").set("border-radius", "3px")
				.set("border", "1px solid #d5d5d5");
		add(wrapperDiv);

		nameLabel = new H2();
		wrapperDiv.add(nameLabel);

		authorYearLabel = new HtmlDiv();
		authorYearLabel.getStyle().set("margin-top", " -8px").set("font-style", "italic");
		wrapperDiv.add(authorYearLabel);

		contentLabel = new HtmlDiv();
		contentLabel.setHeight("700px");
		contentLabel.setWidth(null);
		contentLabel.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		contentLabel.getStyle().set("font-family", "monospace").set("tab-size", "4").set("font-size", "12px")
				.set("overflow-x", "auto").set("display", "-webkit-flex").set("display", "flex")
				.set("-webkit-flex-flow", "column wrap").set("flex-flow", "column wrap");
		wrapperDiv.add(contentLabel);

		embeddedLabel = new HtmlDiv();
		embeddedLabel.setWidth(null);
		embeddedLabel.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		embeddedLabel.getStyle().set("text-align", "center").set("background", "black").set("border-radius", "3px")
				.set("border", "1px solid black");
		add(embeddedLabel);

		ButtonLayout btnLayout = new ButtonLayout();
		add(btnLayout);

		CreateButton addSongBtn = new CreateButton("Přidat", event -> {
			new SongDialog() {
				private static final long serialVersionUID = -4863260002363608014L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					songsPage.selectSong(to.getId());
				}
			}.open();
		});
		btnLayout.add(addSongBtn);
		addSongBtn.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		ModifyButton modifyButton = new ModifyButton("Upravit", event -> {
			new SongDialog(choosenSong) {
				private static final long serialVersionUID = 5264621441522056786L;

				@Override
				protected void onSave(SongTO to) {
					to = songsFacade.saveSong(to);
					showDetail(to);
				}
			}.open();
		});
		btnLayout.add(modifyButton);
		modifyButton.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		DeleteButton deleteButton = new DeleteButton("Smazat", e -> {
			songsFacade.deleteSong(choosenSong.getId());
			songsPage.selectSong(null);
		});
		btnLayout.add(deleteButton);
		deleteButton.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

		ImageButton printButton2 = new ImageButton("Tisk", ImageIcon.PRINT_16_ICON, e -> {
			Path path = createReportPath(choosenSong);
			String uuid = UUID.randomUUID().toString();
			VaadinSession.getCurrent().getSession().setAttribute(ExportRequestHandler.ATTR_PREFIX + uuid, path);
			UI.getCurrent().getPage().open(UIUtils.getPageURL("export/" + uuid));
		});
		btnLayout.add(printButton2);

		Div chordDiv = new Div();
		chordDiv.setVisible(false);
		chordDiv.getStyle().set("position", "absolute").set("background", "white").set("padding", "5px")
				.set("border-radius", "3px").set("border", "1px solid #d5d5d5");
		add(chordDiv);

		Div callbackDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void chordCallback(String chord, double x, double y) {
				chordDiv.setVisible(true);
				chordDiv.removeAll();
				ChordTO to = songsFacade.getChordByName(chord);
				BufferedImage image = ChordImageUtils.drawChord(to, 20);
				String name = "Chord-" + chord;
				chordDiv.add(new Image(new StreamResource(name, () -> {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					try {
						ImageIO.write(image, "png", os);
						return new ByteArrayInputStream(os.toByteArray());
					} catch (IOException e) {
						logger.error("Nezdařilo se vytváření thumbnail akordu", e);
						return null;
					}
				}), name));
				chordDiv.getStyle().set("left", (15 + x) + "px").set("top", y + "px");
			}

			@ClientCallable
			private void hideCallback() {
				chordDiv.setVisible(false);
			}

			@ClientCallable
			private void chordClickCallback(String chord) {
				songsPage.selectChord(songsFacade.getChordByName(chord));
			}

		};
		callbackDiv.setId(JS_DIV_ID);
		add(callbackDiv);
	}

	private Path createReportPath(SongTO choosenSong) {
		JRDataSource jrDataSource = new JasperExportDataSource<SongTO>(new PagedDataSource<SongTO>(1, 1) {
			@Override
			protected List<SongTO> getData(int page, int size) {
				SongTO s = new SongTO(choosenSong.getName(), choosenSong.getAuthor(), choosenSong.getYear(),
						choosenSong.getText().replaceAll("<br/>", "\n"), choosenSong.getId(),
						choosenSong.getPublicated(), choosenSong.getEmbedded());
				return Arrays.asList(new SongTO[] { s });
			}

			@Override
			protected void indicateProgress() {
			}
		});
		return exportsService.createPDFReport(jrDataSource, new HashMap<String, Object>(), "/static/VAADIN/songs/song", ExportType.PRINT);
	}

	public void showDetail(SongTO choosenSong) {
		if (choosenSong == null) {
			nameLabel.setText(null);
			authorYearLabel.setValue(null);
			contentLabel.setValue(null);
		} else {
			nameLabel.setText(choosenSong.getName());
			String value = choosenSong.getAuthor();
			if (choosenSong.getYear() != null && choosenSong.getYear().intValue() > 0)
				value = value + " (" + choosenSong.getYear() + ")";
			authorYearLabel.setValue(value);
			Set<String> chords = songsFacade.getChords(new ChordTO()).stream().map(ChordTO::getName)
					.collect(Collectors.toSet());
			String htmlText = "";
			for (String line : choosenSong.getText().split("<br/>")) {
				boolean chordLine = true;
				for (String chunk : line.split(" +| +|,|\t+"))
					if (StringUtils.isNotBlank(chunk) && !chunk.toLowerCase().matches(
							"(a|b|c|d|e|f|g|h|x|/|#|mi|m|dim|maj|dur|sus|add|[0-9]|-|\\+|\\(|\\)|capo|=|\\.)+")) {
						chordLine = false;
						break;
					}
				for (String c : chords) {
					String chordLink = c;
					chordLink = "<span style='cursor: pointer' onclick='document.getElementById(\"" + JS_DIV_ID
							+ "\").\\$server.chordClickCallback(\"" + c + "\")' "
							+ "onmouseover='document.getElementById(\"" + JS_DIV_ID + "\").\\$server.chordCallback(\""
							+ c + "\", event.clientX, event.clientY)' " + "onmouseout='document.getElementById(\""
							+ JS_DIV_ID + "\").\\$server.hideCallback()'>" + c + "</span>";
					line = line.replaceAll(c + " ", chordLink + " ");
					line = line.replaceAll(c + ",", chordLink + ",");
					line = line.replaceAll(c + "\\)", chordLink + ")");
					line = line.replaceAll(c + "\\(", chordLink + "(");
					line = line.replaceAll(c + "$", chordLink);
				}
				htmlText += chordLine
						? ("<span style='color: blue; white-space: pre; height: 15px'>" + line + "</span>")
						: ("<span style='white-space: pre; padding-right: 20px; height: 15px'>" + line + "</span>");
			}
			contentLabel.setValue(htmlText);
			if (StringUtils.isNotBlank(choosenSong.getEmbedded())) {
				embeddedLabel.setVisible(true);
				String val = "<iframe width=\"100%\" height=\"300\" src=\"https://www.youtube.com/embed/"
						+ choosenSong.getEmbedded()
						+ "\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
				embeddedLabel.setValue(val);
			} else {
				embeddedLabel.setVisible(false);
			}
		}
	}

	public void selectSong(Long songId) {
		choosenSong = songsFacade.getSongById(songId);
		showDetail(choosenSong);
	}

}

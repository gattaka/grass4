package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.grass.core.server.ExportRequestHandler;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.SongsRole;
import cz.gattserver.grass.songs.service.SongsService;
import cz.gattserver.grass.songs.interfaces.ChordTO;
import cz.gattserver.grass.songs.interfaces.SongTO;
import cz.gattserver.grass.songs.util.ChordImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Zpěvník")
@Route(value = "song", layout = MainView.class)
public class SongPage extends Div implements HasUrlParameter<Long> {

    @Serial
    private static final long serialVersionUID = -8084741651374016146L;

    private static final Logger logger = LoggerFactory.getLogger(SongPage.class);

    private static final String SONGS_PAGE_ID = "songs-page-id";

    private final SongsService songsService;
    private final SecurityService securityService;

    private H2 nameLabel;
    private HtmlDiv authorYearLabel;
    private HtmlDiv contentLabel;
    private HtmlDiv embeddedLabel;

    private SongTO choosenSong;

    private Div chordDiv;

    public SongPage(SongsService songsService, SecurityService securityService) {
        this.songsService = songsService;
        this.securityService = securityService;

        setId(SONGS_PAGE_ID);
    }

    @Override
    public void setParameter(BeforeEvent event, Long id) {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        TabsMenu tabsMenu = new TabsMenu();
        layout.add(tabsMenu);
        tabsMenu.selectSongTab();

        Div wrapperDiv = new Div();
        wrapperDiv.getStyle().set("padding", "10px").set("background", "white").set("border-radius", "3px")
                .set("border", "1px solid #d5d5d5");
        wrapperDiv.addClassNames(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(wrapperDiv);

        nameLabel = new H2();
        wrapperDiv.add(nameLabel);

        authorYearLabel = new HtmlDiv();
        authorYearLabel.getStyle().set("margin-top", " -8px").set("font-style", "italic");
        wrapperDiv.add(authorYearLabel);

        contentLabel = new HtmlDiv();
        contentLabel.setHeight(700, Unit.PIXELS);
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
        layout.add(embeddedLabel);

        Div btnLayout = componentFactory.createButtonLayout();
        btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(btnLayout);

        Button modifyButton = componentFactory.createEditButton(e -> new SongDialog(choosenSong, to -> {
            to = songsService.saveSong(to);
            showDetail(to);
        }).open());
        btnLayout.add(modifyButton);
        modifyButton.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        Button deleteButton = componentFactory.createDeleteButton(e -> songsService.deleteSong(choosenSong.getId()));
        btnLayout.add(deleteButton);
        deleteButton.setVisible(securityService.getCurrentUser().getRoles().contains(SongsRole.SONGS_EDITOR));

        Button printButton = new Button("Tisk", VaadinIcon.PRINT.create(), e -> {
            Path path = createReportPath(choosenSong, false);
            String uuid = UUID.randomUUID().toString();
            VaadinSession.getCurrent().getSession().setAttribute(ExportRequestHandler.ATTR_PREFIX + uuid, path);
            UI.getCurrent().getPage().open(UIUtils.getPageURL("export/" + uuid));
        });
        btnLayout.add(printButton);

        Button printButton2 = new Button("Tisk (dva sloupce)", VaadinIcon.PRINT.create(), e -> {
            Path path = createReportPath(choosenSong, true);
            String uuid = UUID.randomUUID().toString();
            VaadinSession.getCurrent().getSession().setAttribute(ExportRequestHandler.ATTR_PREFIX + uuid, path);
            UI.getCurrent().getPage().open(UIUtils.getPageURL("export/" + uuid));
        });
        btnLayout.add(printButton2);

        chordDiv = new Div();
        chordDiv.setVisible(false);
        chordDiv.getStyle().set("position", "absolute").set("background", "white").set("padding", "5px")
                .set("border-radius", "3px").set("border", "1px solid #d5d5d5");
        layout.add(chordDiv);

        choosenSong = songsService.getSongById(id);
        showDetail(choosenSong);
    }

    @ClientCallable
    private void chordCallback(String chord, double x, double y) {
        chordDiv.setVisible(true);
        chordDiv.removeAll();
        ChordTO to = songsService.getChordByName(chord);
        String name = "Chord-" + chord;
        chordDiv.add(new Image(DownloadHandler.fromInputStream(e -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                BufferedImage image = ChordImageUtils.drawChord(to, 20);
                ImageIO.write(image, "png", os);
                return new DownloadResponse(new ByteArrayInputStream(os.toByteArray()), name, null, -1);
            } catch (IOException ex) {
                logger.error("Nezdařilo se vytváření thumbnail akordu", ex);
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
        UI.getCurrent().navigate(ChordsPage.class, chord);
    }

    private Path createReportPath(SongTO choosenSong, boolean twoColumn) {
        SongTO s =
                new SongTO(choosenSong.getId(), choosenSong.getName(), choosenSong.getAuthor(), choosenSong.getYear(),
                        choosenSong.getText().replaceAll("<br/>", "\n"), choosenSong.getPublicated(),
                        choosenSong.getEmbedded());
        return songsService.print(s, twoColumn);
    }

    public void showDetail(SongTO choosenSong) {
        if (choosenSong == null) {
            nameLabel.setText(null);
            authorYearLabel.setValue(null);
            contentLabel.setValue(null);
        } else {
            nameLabel.setText(choosenSong.getName());
            String value = choosenSong.getAuthor();
            if (choosenSong.getYear() != null && choosenSong.getYear() > 0)
                value = value + " (" + choosenSong.getYear() + ")";
            authorYearLabel.setValue(value);
            Set<String> chords =
                    songsService.getChords(new ChordTO()).stream().map(ChordTO::getName).collect(Collectors.toSet());
            StringBuilder htmlText = new StringBuilder();
            for (String line : choosenSong.getText().split("<br/>")) {
                boolean chordLine = true;
                for (String chunk : line.split(" +| +|,|\t+"))
                    if (StringUtils.isNotBlank(chunk) && !chunk.toLowerCase().matches(
                            "(a|b|c|d|e|f|g|h|x|/|#|mi|m|dim|maj|dur|sus|add|[0-9]|-|\\+|\\(|\\)|capo|=|\\.)+")) {
                        chordLine = false;
                        break;
                    }
                for (String c : chords) {
                    String chordLink = createChordLink(c);
                    line = line.replaceAll(c + " ", chordLink + " ");
                    line = line.replaceAll(c + ",", chordLink + ",");
                    line = line.replaceAll(c + "\\)", chordLink + ")");
                    line = line.replaceAll(c + "\\(", chordLink + "(");
                    line = line.replaceAll(c + "$", chordLink);
                }
                htmlText.append(
                        chordLine ? ("<span style='color: blue; white-space: pre; height: 15px'>" + line + "</span>") :
                                ("<span style='white-space: pre; padding-right: 20px; height: 15px'>" + line +
                                        "</span>"));
            }
            contentLabel.setValue(htmlText.toString());
            if (StringUtils.isNotBlank(choosenSong.getEmbedded())) {
                embeddedLabel.setVisible(true);
                String val = "<iframe width=\"100%\" height=\"300\" src=\"https://www.youtube.com/embed/" +
                        choosenSong.getEmbedded() +
                        "\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; " +
                        "gyroscope; picture-in-picture\" allowfullscreen></iframe>";
                embeddedLabel.setValue(val);
            } else {
                embeddedLabel.setVisible(false);
            }
        }
    }

    private static @NonNull String createChordLink(String c) {
        String chordLink;
        chordLink = "<span style='cursor: pointer' onclick='document.getElementById(\"" + SONGS_PAGE_ID +
                "\").\\$server.chordClickCallback(\"" + c + "\")' " +
                "onmouseover='let bound = document.body.getBoundingClientRect(); document" +
                ".getElementById" + "(\"" + SONGS_PAGE_ID + "\").\\$server.chordCallback(\"" + c +
                "\", event.clientX - bound.x, event.clientY - bound.y)' " + "onmouseout='document" +
                ".getElementById(\"" + SONGS_PAGE_ID + "\").\\$server.hideCallback()'>" + c + "</span>";
        return chordLink;
    }
}
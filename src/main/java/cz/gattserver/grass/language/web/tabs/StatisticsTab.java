package cz.gattserver.grass.language.web.tabs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass.language.facades.LanguageFacade;
import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.web.ChartUtils;
import cz.gattserver.grass.language.web.LanguagePage;

public class StatisticsTab extends Div {

    private static final long serialVersionUID = 2977436873976010065L;

    private static final Logger logger = LoggerFactory.getLogger(LanguagePage.class);

    @Autowired
    private LanguageFacade languageFacade;

    public StatisticsTab(Long langId) {
        SpringContextHelper.inject(this);

        LanguageItemTO to = new LanguageItemTO();
        to.setLanguage(langId);
        to.setType(ItemType.WORD);
        int words = languageFacade.countLanguageItems(to);
        to.setType(ItemType.PHRASE);
        int phrases = languageFacade.countLanguageItems(to);

        add("Slovíček: " + words);
        final BufferedImage wordsImage = ChartUtils.drawChart(languageFacade.getStatisticsItems(ItemType.WORD, langId));
        Image wordsImg = new Image(DownloadHandler.fromInputStream(e -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(wordsImage, "png", os);
                return new DownloadResponse(new ByteArrayInputStream(os.toByteArray()), "word", null, -1);
            } catch (IOException ex) {
                logger.error("Nezdařilo se vytváření grafu statistiky", ex);
                return null;
            }
        }), "wordsImage.png");
        wordsImg.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(wordsImg);

        Div header = new Div();
        header.add("Frází: " + phrases);
        header.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(header);
        final BufferedImage phrasesImage =
                ChartUtils.drawChart(languageFacade.getStatisticsItems(ItemType.PHRASE, langId));
        Image phrasesImg = new Image(DownloadHandler.fromInputStream(e -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(phrasesImage, "png", os);
                return new DownloadResponse(new ByteArrayInputStream(os.toByteArray()), "phrases", null, -1);
            } catch (IOException ex) {
                logger.error("Nezdařilo se vytváření grafu statistiky", ex);
                return null;
            }
        }), "phrasesImage.png");
        phrasesImg.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(phrasesImg);

        header = new Div();
        header.add("Položek celkem: " + (words + phrases));
        header.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(header);

        final BufferedImage itemsImage = ChartUtils.drawChart(languageFacade.getStatisticsItems(null, langId));
        Image itemsImg = new Image(DownloadHandler.fromInputStream(e -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(itemsImage, "png", os);
                return new DownloadResponse(new ByteArrayInputStream(os.toByteArray()), "items", null, -1);
            } catch (IOException ex) {
                logger.error("Nezdařilo se vytváření grafu statistiky", ex);
                return null;
            }
        }), "itemsImage.png");
        itemsImg.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(itemsImg);
    }

}

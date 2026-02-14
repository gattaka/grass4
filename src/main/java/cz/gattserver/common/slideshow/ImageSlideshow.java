package cz.gattserver.common.slideshow;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ImageSlideshow<T extends SlideshowItem> extends Div {

    private static final Logger logger = LoggerFactory.getLogger(ImageSlideshow.class);

    protected int currentIndex;
    protected int totalCount;
    protected Div itemLabel;
    protected Div itemLayout;
    protected Div itemExifLayout;

    private Consumer<Integer> pageUpdateListener;
    private Function<Integer, T> itemByIndexProvider;
    private Function<T, String> itemSlideshowURLProvider;
    private Function<T, String> itemDetailURLProvider;
    private T currentItemTO;

    protected boolean closePrevented = false;

    protected List<ShortcutRegistration> regs = new ArrayList<>();

    public ImageSlideshow(int count, Consumer<Integer> pageUpdateListener, Function<Integer, T> itemByIndexProvider,
                          Function<T, String> itemSlideshowURLProvider, Function<T, String> itemDetailURLProvider) {
        this.totalCount = count;
        this.pageUpdateListener = pageUpdateListener;
        this.itemByIndexProvider = itemByIndexProvider;
        this.itemSlideshowURLProvider = itemSlideshowURLProvider;
        this.itemDetailURLProvider = itemDetailURLProvider;

        setId("image-slideshow-div");

        itemLabel = new Div();
        itemLabel.setId("image-slideshow-item-label-div");
        add(itemLabel);
        addClickListener(e -> close());

        Div wrapperDiv = new Div();
        wrapperDiv.setId("image-slideshow-item-wrapper-div");
        add(wrapperDiv);

        itemExifLayout = new Div();
        itemExifLayout.setId("image-exif-div");
        add(itemExifLayout);

        String jsDivId = "image-slideshow-item-div";
        itemLayout = new Div() {
            @Serial
            private static final long serialVersionUID = -604140874889079656L;

            @ClientCallable
            private void prev() {
                prevItem();
            }

            @ClientCallable
            private void next() {
                nextItem();
            }
        };
        itemLayout.setId(jsDivId);
        wrapperDiv.add(itemLayout);

        Div prevItemButton = new Div("<");
        prevItemButton.addClickListener(e -> {
            prevItem();
            preventClose();
        });
        prevItemButton.setId("image-slideshow-prev-item-button");
        wrapperDiv.add(prevItemButton);

        Div nextItemButton = new Div(">");
        nextItemButton.addClickListener(e -> {
            nextItem();
            preventClose();
        });
        nextItemButton.setId("image-slideshow-next-item-button");
        wrapperDiv.add(nextItemButton);

        UI.getCurrent().getPage().executeJs(
                "let cont = document.querySelector('#image-slideshow-item-div');"/*		*/ + "let NF = 30;"
                        /*		*/ + "let N = 1;"
                        /*		*/ + ""
                        /*		*/ + "let i = 0, x0 = null, locked = false, w, ini, fin, rID = null, anf;"
                        /*		*/ + ""
                        /*		*/ + "function smooth(k) {"
                        /*		*/ + "	return .5 * (Math.sin((k - .5) * Math.PI) + 1);"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function stopAni() {"
                        /*		*/ + "	cancelAnimationFrame(rID);"
                        /*		*/ + "	rID = null;"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function stop() {"
                        /*		*/ + "  stopAni();"
                        /*		*/ + "  i = 0;"
                        /*		*/ + "  anf = 0;"
                        /*		*/ + "	fin = i;"
                        /*		*/ + "	x0 = null;"
                        /*		*/ + "	locked = false;"
                        /*		*/ + "	cont.style.setProperty('--i', 0);"
                        /*		*/ + "	console.log('stop');"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function setI(n) {"
                        /*		*/ + "	cont.style.setProperty('--i', Number.isNaN(n) ? 0 : n);"
                        /*		*/ + "  if (n < -.25) {"
                        /*		*/ + "		document.getElementById('" + jsDivId + "').$server.prev();"
                        /*		*/ + "  	stop();"
                        /*		*/ + "  }"
                        /*		*/ + "  if (n > .25) {"
                        /*		*/ + "		document.getElementById('" + jsDivId + "').$server.next();"
                        /*		*/ + "  	stop();"
                        /*		*/ + "  }"
                        /*		*/ + "	console.log(i);"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function ani(cf = 0) {"
                        /*		*/ + "  setI(ini + (fin - ini) * smooth(cf / anf));"
                        /*		*/ + "	if (cf === anf) {"
                        /*		*/ + "		stopAni();"
                        /*		*/ + "		return;"
                        /*		*/ + "	}"
                        /*		*/ + "	rID = requestAnimationFrame(ani.bind(this, ++cf));"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function unify(e) { return e.changedTouches ? e.changedTouches[0] : e };"
                        /*		*/ + ""
                        /*		*/ + "function lock(e) {"
                        /*		*/ + "	x0 = unify(e).clientX;"
                        /*		*/ + "	locked = true;"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function drag(e) {"
                        /*		*/ + "	e.preventDefault();"
                        /*		*/ + "	if (locked) {"
                        /*		*/ + "		let dx = unify(e).clientX - x0, f = +(dx / w).toFixed(2);"
                        /*		*/ + "		setI(i - f);"
                        /*		*/ + "	}"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function move(e) {"
                        /*		*/ + "	if (locked) {"
                        /*		*/ + "		let dx = unify(e).clientX - x0;"
                        /*		*/ + "		let s = Math.sign(dx);"
                        /*		*/ + "		let f = +(s * dx / w).toFixed(2);"
                        /*		*/ + "		ini = i - s * f;"
                        /*		*/ + "		if((i > 0 || s < 0) && (i < N - 1 || s > 0) && f > .2) {"
                        /*		*/ + "			i -= s;"
                        /*		*/ + "			f = 1 - f;"
                        /*		*/ + "		}"
                        /*		*/ + "		fin = i;"
                        /*		*/ + "		anf = Math.round(f * NF);"
                        /*		*/ + "		ani();"
                        /*		*/ + "		x0 = null;"
                        /*		*/ + "		locked = false;"
                        /*		*/ + "	}"
                        /*		*/ + "};"
                        /*		*/ + ""
                        /*		*/ + "function size() { w = window.innerWidth };"
                        /*		*/ + "addEventListener('resize', size, false);"
                        /*		*/ + "" + "size();" + "cont.style.setProperty('--n', N);"
                        /*		*/ + "cont.addEventListener('mousedown', lock, false);"
                        /*		*/ + "cont.addEventListener('touchstart', lock, false);"
                        /*		*/ + "cont.addEventListener('mousemove', drag, false);"
                        /*		*/ + "cont.addEventListener('touchmove', drag, false);"
                        /*		*/ + "cont.addEventListener('mouseup', move, false);"
                        /*		*/ + "cont.addEventListener('touchend', move, false);");

        ComponentFactory componentFactory = new ComponentFactory();
        Div closeBtn = componentFactory.createInlineButton("Zavřít", e -> close());
        Div detailButton = componentFactory.createInlineButton("Detail",
                e -> UI.getCurrent().getPage().open(itemDetailURLProvider.apply(currentItemTO)));
        Div btnDiv = new Div(detailButton, closeBtn);
        btnDiv.setId("image-slideshow-item-close-div");
        add(btnDiv);
    }

    private Component createItemSlide(T itemTO) {
        // vytvoř odpovídající komponentu pro zobrazení
        // obrázku nebo videa
        switch (itemTO.getType()) {
            case VIDEO:
                return createVideoSlide(itemTO);
            case IMAGE:
            default:
                return createImageSlide(itemTO);
        }
    }

    public void showItem(int index) {
        currentIndex = index;
        try {
            pageUpdateListener.accept(currentIndex);
            currentItemTO = itemByIndexProvider.apply(index);

            itemExifLayout.removeAll();
            ExifInfoTO exifInfoTO = currentItemTO.getExifInfoTO();
            if (exifInfoTO != null) {
                if (exifInfoTO.getDate() != null) {
                    Div dateDiv = new Div();
                    itemExifLayout.add(dateDiv);
                    dateDiv.add(new Div("Datum pořízení:"));
                    dateDiv.add(
                            new Div(exifInfoTO.getDate().format(DateTimeFormatter.ofPattern("dd. MM. yyyy HH:mm:ss"))));
                }
                if (exifInfoTO.getDeviceMaker() != null && exifInfoTO.getDeviceModel() != null) {
                    Div dateDiv = new Div();
                    itemExifLayout.add(dateDiv);
                    dateDiv.add(new Div("Fotoaparát:"));
                    dateDiv.add(new Div(exifInfoTO.getDeviceMaker() + ", " + exifInfoTO.getDeviceModel()));
                }
                if (exifInfoTO.getLongitude() != null && exifInfoTO.getLatitude() != null) {
                    Div dateDiv = new Div();
                    itemExifLayout.add(dateDiv);
                    dateDiv.add(new Div("Místo:"));
                    dateDiv.add(new Div(exifInfoTO.getLatitude() + " N, " + exifInfoTO.getLongitude() + " E"));
                    dateDiv.add(new Anchor("https://www.google.com/maps?z=15&t=h&q=" + exifInfoTO.getLatitude() + "+" +
                            exifInfoTO.getLongitude(), "Google Maps", AnchorTarget.BLANK));
                }
            }
            Component slideshowComponent = createItemSlide(currentItemTO);
            itemLayout.removeAll();
            itemLayout.add(slideshowComponent);
            itemLabel.setText((index + 1) + "/" + totalCount + " " + currentItemTO.getName());
        } catch (Exception e) {
            logger.error("Chyba při zobrazování slideshow položky fotogalerie", e);
            UIUtils.showWarning("Zobrazení položky se nezdařilo");
            close();
        }
    }

    private Component createVideoSlide(T itemTO) {
        String videoURL = itemDetailURLProvider.apply(itemTO);
        String videoString =
                "<video id=\"video\" preload controls>" + "<source src=\"" + videoURL + "\" type=\"video/mp4\">" +
                        "</video>";
        HtmlDiv video = new HtmlDiv(videoString);
        video.addClickListener(e -> preventClose());
        return video;
    }

    private Component createImageSlide(T itemTO) {
        Image embedded;
        if (itemTO.getName().toLowerCase().endsWith(".xcf")) {
            embedded = new Image("img/gimp.png", "XCF file");
        } else if (itemTO.getName().toLowerCase().endsWith(".otf") || itemTO.getName().toLowerCase().endsWith(".ttf")) {
            embedded = new Image("img/font.png", "Font file");
        } else {
            embedded = new Image(itemSlideshowURLProvider.apply(itemTO), itemTO.getName());
        }

        embedded.addClickListener(e -> {
            preventClose();
            UI.getCurrent().getPage().open(itemDetailURLProvider.apply(itemTO));
        });
        embedded.getStyle().set("cursor", "pointer");
        return embedded;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        regs.add(Shortcuts.addShortcutListener(this, this::prevItem, Key.ARROW_LEFT));
        regs.add(Shortcuts.addShortcutListener(this, this::nextItem, Key.ARROW_RIGHT));
        regs.add(Shortcuts.addShortcutListener(this, this::close, Key.ESCAPE));
    }

    protected void prevItem() {
        if (currentIndex > 0) showItem(currentIndex - 1);
    }

    protected void nextItem() {
        if (currentIndex < totalCount - 1) showItem(currentIndex + 1);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        for (ShortcutRegistration r : regs)
            r.remove();
    }

    protected void close() {
        if (!closePrevented) ((HasComponents) getParent().get()).remove(ImageSlideshow.this);
        closePrevented = false;
    }

    protected void preventClose() {
        closePrevented = true;
    }

}
package cz.gattserver.grass.core.ui.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.ExtendedClientDetails;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

import com.vaadin.flow.shared.Registration;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.common.vaadin.dialogs.InfoDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import jakarta.servlet.http.HttpServletRequest;

public class UIUtils {

	private static final Logger logger = LoggerFactory.getLogger(UIUtils.class);

	public static final String SPACING_CSS_VAR = "var(--lumo-space-m)";
	public static final String BUTTON_SIZE_CSS_VAR = "var(--lumo-button-size)";
	public static final String FIELD_SIZE_CSS_VAR = "var(--lumo-text-field-size)";
	public static final String FIELD_CAPTION_SIZE_CSS_VAR = "1.5em";

	public static final String TOP_MARGIN_CSS_CLASS = "top-margin";
	public static final String TOP_CLEAN_CSS_CLASS = "top-clean";
	public static final String TOP_PULL_CSS_CLASS = "top-pull";
	public static final String THUMBNAIL_200_CSS_CLASS = "thumbnail-200";
	public static final String BUTTON_LINK_CSS_CLASS = "button-link";

	public static final String GRID_ICON_CSS_CLASS = "grid-icon-img";

	private UIUtils() {
	}

	/**
	 * Zjistí, zda je používáno mobilní zařízení
	 */
	public static boolean isMobileDevice() {
		WebBrowser wb = VaadinSession.getCurrent().getBrowser();
		ExtendedClientDetails ecd = UI.getCurrent().getInternals().getExtendedClientDetails();
		if (ecd != null && !ecd.isTouchDevice())
			return false;
		return wb.isIPhone() || wb.isAndroid() || wb.isWindowsPhone() || ecd != null && ecd.isIPad();
	}

	/**
	 * Scroll v gridu na pozici
	 */
	public static void scrollGridToIndex(Grid<?> grid, int index) {
		//		UI.getCurrent().getPage().executeJs("$0._scrollToIndex(" + index + ")", grid.getElement());
		grid.scrollToIndex(index);
	}

	/**
	 * Ostyluje Grid tak, jak vypadají všechny tabulky v systému
	 */
	public static void applyGrassDefaultStyle(Grid<?> grid) {
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
	}

	/**
	 * Přidá styl, aby pole bylo malé
	 */
	public static TextField asSmall(TextField textField) {
		textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		return textField;
	}

	/**
	 * Přidá styl, aby pole bylo malé
	 */
	public static DatePicker asSmall(DatePicker datePicker) {
		datePicker.getElement().setAttribute("theme", TextFieldVariant.LUMO_SMALL.getVariantName());
		return datePicker;
	}

	/**
	 * Přidá styl, aby combo bylo malé
	 */
	public static <T> ComboBox<T> asSmall(ComboBox<T> comboBox) {
		comboBox.getElement().setAttribute("theme", TextFieldVariant.LUMO_SMALL.getVariantName());
		return comboBox;
	}

	/**
	 * Přidá filtrovací pole do záhlaví gridu
	 */
	public static TextField addHeaderTextField(
			HeaderCell cell,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
		TextField field = UIUtils.asSmall(new TextField());
		field.setWidthFull();
		field.addValueChangeListener(listener);
		field.setClearButtonVisible(true);
		field.setValueChangeMode(ValueChangeMode.EAGER);
		cell.setComponent(field);
		return field;
	}

	/**
	 * Přidá filtrovací datepicker do záhlaví gridu
	 */
	public static DatePicker addHeaderDatePicker(
			HeaderCell cell,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		DatePicker field = UIUtils.asSmall(new DatePicker());
		field.setWidthFull();
		field.addValueChangeListener(listener);
		cell.setComponent(field);
		return field;
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> ComboBox<T> addHeaderComboBox(
			HeaderCell cell, Class<T> enumType,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		return addHeaderComboBox(cell, enumType.getEnumConstants(), itemLabelGenerator, listener);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> ComboBox<T> addHeaderComboBox(
			HeaderCell cell, T[] values,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		return addHeaderComboBox(cell, Arrays.asList(values), itemLabelGenerator, listener);
	}

	/**
	 * Přidá filtrovací combo do záhlaví gridu
	 */
	public static <T extends Enum<T>> ComboBox<T> addHeaderComboBox(
			HeaderCell cell, Collection<T> values,
			ItemLabelGenerator<T> itemLabelGenerator,
			HasValue.ValueChangeListener<? super ComponentValueChangeEvent<ComboBox<T>, T>> listener) {
		ComboBox<T> combo = UIUtils.asSmall(new ComboBox<>(null, values));
		combo.setWidthFull();
		combo.setRequired(false);
		combo.setClearButtonVisible(true);
		combo.addValueChangeListener(listener);
		combo.setItemLabelGenerator(itemLabelGenerator);
		cell.setComponent(combo);
		return combo;
	}

	/**
	 * Přejde na stránku
	 */
	public static void redirect(String uri) {
		UI.getCurrent().getPage().setLocation(uri);
	}

	/**
	 * Notifikace pomocí {@link Notification}
	 */
	public static void showSilentInfo(String caption) {
		Notification.show(caption);
	}

	/**
	 * Notifikace pomocí {@link InfoDialog}
	 */
	public static void showInfo(String caption) {
		new InfoDialog(caption).open();
	}

	/**
	 * Notifikace varování pomocí {@link WarnDialog}
	 */
	public static void showWarning(String caption) {
		new WarnDialog(caption).open();
	}

	/**
	 * Notifikace chyby pomocí {@link ErrorDialog}
	 */
	public static void showError(String caption) {
		new ErrorDialog(caption).open();
	}

	/**
	 * Nahraje více JS skriptů, synchronně za sebou (mohou se tedy navzájem na sebe odkazovat a bude zaručeno, že 1.
	 * skript bude celý nahrán před 2. skriptem, který využívá jeho funkcí)
	 *
	 * @param scripts skripty, které budou nahrány
	 */
	public static PendingJavaScriptResult loadJS(JScriptItem... scripts) {
		return loadJS(Arrays.asList(scripts));
	}

	/**
	 * Nahraje více JS skriptů, synchronně za sebou (mohou se tedy navzájem na sebe odkazovat a bude zaručeno, že 1.
	 * skript bude celý nahrán před 2. skriptem, který využívá jeho funkcí)
	 *
	 * @param scripts skripty, které budou nahrány
	 */
	public static PendingJavaScriptResult loadJS(List<JScriptItem> scripts) {
		StringBuilder builder = new StringBuilder();
		buildJSBatch(builder, 0, scripts);
		return UI.getCurrent().getPage().executeJs(builder.toString());
	}

	private static void buildJSBatch(StringBuilder builder, int index, List<JScriptItem> scripts) {
		if (index >= scripts.size())
			return;

		JScriptItem js = scripts.get(index);
		String chunk = js.getScript();
		// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
		// obejít problém se závislosí pluginů na úložišti theme apod. a
		// přitom umožnit aby se JS odkazovali na externí zdroje
		if (!js.isPlain()) {
			if (!chunk.toLowerCase().startsWith("http://") && !chunk.toLowerCase().startsWith("https://")) {
				chunk = "\"" + getContextPath() + "/VAADIN/" + chunk + "\"";
			} else {
				chunk = "\"" + chunk + "\"";
			}
			builder.append("$.getScript(").append(chunk).append(", function(){");
			buildJSBatch(builder, index + 1, scripts);
			builder.append("});");
		} else {
			builder.append(chunk);
			buildJSBatch(builder, index + 1, scripts);
		}
	}

	/**
     * Nahraje CSS
     *
     * @param link odkaz k css souboru - relativní, absolutní (http://...)
     * @return
     */
	public static Registration loadCSS(String link) {
        // https://vaadin.com/docs/latest/styling/advanced/dynamic-stylesheets
        return UI.getCurrent().getPage().addStyleSheet(link);
    }

	private static String getClientIp(HttpServletRequest request) {
		String remoteAddr = request.getHeader("X-FORWARDED-FOR");
		if (StringUtils.isBlank(remoteAddr))
			remoteAddr = request.getRemoteAddr();
		return remoteAddr;
	}

	public static HttpServletRequest getHttpServletRequest() {
		VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
		VaadinServletRequest vaadinServletRequest = (VaadinServletRequest) vaadinRequest;
		HttpServletRequest httpServletRequest = vaadinServletRequest.getHttpServletRequest();
		return httpServletRequest;
	}

	public static String getIPAddress() {
		String ipAddress = getClientIp(getHttpServletRequest());
		return ipAddress;
	}

	public static String getURLBase() {
		HttpServletRequest httpServletRequest = getHttpServletRequest();

		// Příklad:
		// aplikace běží na adrese http://-server-:8180/web
		// request je na http://-server-:8180/web/fm/Android
		// pak:
		// vrátí /web/fm/Android
		String requestURI = httpServletRequest.getRequestURI();

		// vrátí /fm/Android
		String pathInfo = httpServletRequest.getPathInfo();

		// vrátí /web
		String contextPath = httpServletRequest.getContextPath();

		// vrátí http://-server-:8180/web/fm/Android
		String fullURL = httpServletRequest.getRequestURL().toString();

		// např. http://-server-:8180
		//String urlBase = fullURL.substring(0, fullURL.length() - pathInfo.length());

		// Tady je ale problém, protože pokud aplikace běží na -server- za reverse proxy,
		// pak se místo http://-server-:8180 může vrátit např. lokální http://127.0.0.1:60111
		// Ve výsledku je tedy nejlepší

		String urlBase = SpringContextHelper.getBean(Environment.class).getProperty("grass.address");

		return urlBase + contextPath;
	}

	public static String getContextPath() {
		return VaadinRequest.getCurrent().getContextPath();
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix dle pageFactory
	 */
	public static String getPageURL(PageFactory pageFactory) {
		return getContextPath() + "/" + pageFactory.getPageName();
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix
	 */
	public static String getPageURL(String suffix) {
		return getContextPath() + "/" + suffix;
	}

	/**
	 * Získá URL stránky. Kořen webu + suffix dle pageFactory + relativní URL
	 */
	public static String getPageURL(PageFactory pageFactory, String... relativeURLs) {
		if (relativeURLs.length == 1) {
			return getPageURL(pageFactory) + "/" + relativeURLs[0];
		} else {
			StringBuilder buffer = new StringBuilder();
			buffer.append(getPageURL(pageFactory));
			for (String relativeURL : relativeURLs) {
				if (relativeURL != null) {
					buffer.append("/");
					buffer.append(relativeURL);
				}
			}
			return buffer.toString();
		}
	}

	/**
	 * Články a jiné obsahy můžou přidat na stránku různé JS skritpy a DOM elementy,
	 * které při další vaadin-router navigaci zůstanou i na dalších stránkách
	 * to není žádoucí a nelze zajistit spolehlivě clean stránky od těchto prvků,
	 * řešením je tedy z pozice ArticleViewer všechny vykreslené Anchor elementy
	 * označit jako vaadin router-ignore, aby další navigace byla vždy s full reload
	 */
	public static void turnOffRouterAnchors() {
		UI.getCurrent().getPage().executeJs("""
				let elements = document.getElementsByTagName("a");
				for (let i = 0; i < elements.length; i++) {
					let element = elements.item(i);
					if (!element.hasAttribute("router-ignore"))
						element.setAttribute("router-ignore","");
				}
				""");
	}
}

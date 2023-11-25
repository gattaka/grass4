package cz.gattserver.grass.songs.ui;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.interfaces.SongTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

public class SongDialog extends EditWebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	@Autowired
	private SongsService songsFacade;

	public SongDialog(Consumer<SongTO> onSave) {
		this(null, onSave);
	}

	public SongDialog(final SongTO originalTO, Consumer<SongTO> onSave) {
		SpringContextHelper.inject(this);
		setWidth("600px");

		SongTO formTO = new SongTO();
		formTO.setYear(0);
		formTO.setPublicated(true);

		Binder<SongTO> binder = new Binder<>(SongTO.class);
		binder.setBean(formTO);

		final TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(SongTO::getName, SongTO::setName);
		nameField.setWidthFull();
		add(nameField);

		final TextField authorField = new TextField("Autor");
		binder.forField(authorField).bind(SongTO::getAuthor, SongTO::setAuthor);
		authorField.setWidthFull();

		final TextField yearField = new TextField("Rok");
		binder.forField(yearField).withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.bind(SongTO::getYear, SongTO::setYear);
		yearField.setWidthFull();

		final Checkbox publicatedCheckBox = new Checkbox("Veřejný text");
		binder.forField(publicatedCheckBox).bind(SongTO::getPublicated, SongTO::setPublicated);
		publicatedCheckBox.setWidthFull();
		publicatedCheckBox.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);

		HorizontalLayout authorYearLayout = new HorizontalLayout(authorField, yearField, publicatedCheckBox);
		authorYearLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		add(authorYearLayout);

		final TextArea textField = new TextArea("Text");
		binder.forField(textField).asRequired().bind(SongTO::getText, SongTO::setText);
		textField.setWidthFull();
		textField.setHeight("500px");
		textField.getStyle().set("font-family", "monospace").set("tab-size", "4").set("font-size", "12px");
		add(textField);

		final TextArea embeddedField = new TextArea("Embedded");
		binder.forField(embeddedField).bind(SongTO::getEmbedded, SongTO::setEmbedded);
		embeddedField.setWidthFull();
		embeddedField.setHeight("100px");
		embeddedField.getStyle().set("font-family", "monospace").set("tab-size", "4").set("font-size", "12px");
		embeddedField.setPlaceholder("YouTube video ID (tZtPcQJkEcU,...)");
		add(embeddedField);

		add(new SaveCloseLayout(event -> save(originalTO, binder, onSave), e -> close()));

		if (originalTO != null) {
			binder.readBean(originalTO);
			textField.setValue(songsFacade.breaklineToEol(originalTO.getText()));
		}
	}

	private void save(SongTO originalTO, Binder<SongTO> binder, Consumer<SongTO> onSave) {
		SongTO writeTO = originalTO == null ? new SongTO() : originalTO;
		if (binder.writeBeanIfValid(writeTO)) {
			try {
				onSave.accept(writeTO);
				close();
			} catch (Exception ve) {
				new ErrorDialog("Uložení se nezdařilo").open();
				throw ve;
			}
		}
	}
}
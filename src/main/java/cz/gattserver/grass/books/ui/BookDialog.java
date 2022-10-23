package cz.gattserver.grass.books.ui;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import cz.gattserver.grass.books.util.ImageUtils;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import cz.gattserver.grass.core.ui.components.button.ModifyButton;
import cz.gattserver.grass.core.ui.util.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.web.common.ui.dialogs.EditWebDialog;
import cz.gattserver.web.common.ui.dialogs.ErrorDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BookDialog extends EditWebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	private static final Logger logger = LoggerFactory.getLogger(BookDialog.class);

	private VerticalLayout imageLayout;
	private Upload upload;
	private Image image;

	protected abstract void onSave(BookTO to);

	public BookDialog() {
		this(null);
	}

	public BookDialog(final BookTO originalTO) {
		super();

		BookTO formTO = new BookTO();

		Binder<BookTO> binder = new Binder<>();
		binder.setBean(formTO);

		imageLayout = new VerticalLayout();
		imageLayout.setPadding(false);
		addComponent(imageLayout);

		image = new Image();
		image.setVisible(false);

		// https://vaadin.com/components/vaadin-upload/java-examples
		MemoryBuffer buffer = new MemoryBuffer();
		upload = new Upload(buffer);
		upload.setMaxFileSize(2000000);
		upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png");
		upload.addSucceededListener(e -> {
			try {
				// vytvoř miniaturu
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageUtils.resizeImageFile(e.getFileName(), buffer.getInputStream(), bos, 400, 400);
				formTO.setImage(bos.toByteArray());
				placeImage(formTO);
			} catch (IOException ex) {
				String err = "Nezdařilo se nahrát obrázek nápoje";
				logger.error(err, ex);
				UIUtils.showError(err);
			}
		});

		if (originalTO == null || originalTO.getImage() == null)
			placeUpload();
		else {
			placeImage(originalTO);
			formTO.setImage(originalTO.getImage());
		}

		HorizontalLayout btnsLayout = new HorizontalLayout();
		btnsLayout.setSpacing(false);
		btnsLayout.setPadding(false);
		btnsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		btnsLayout.setWidthFull();
		addComponent(btnsLayout);

		if (originalTO != null)
			btnsLayout.add(new ModifyButton(event -> save(originalTO, binder)));
		else
			btnsLayout.add(new CreateButton(event -> save(originalTO, binder)));

		btnsLayout.add(new CloseButton(e -> close()));

		VerticalLayout fieldsLayout = createForm(binder);
		fieldsLayout.setPadding(false);
		fieldsLayout.add(btnsLayout);
		HorizontalLayout mainLayout = new HorizontalLayout(imageLayout, fieldsLayout);
		addComponent(mainLayout);

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private void save(BookTO originalTO, Binder<BookTO> binder) {
		try {
			BookTO writeTO = originalTO == null ? new BookTO() : originalTO;
			binder.writeBean(writeTO);
			writeTO.setImage(binder.getBean().getImage());
			onSave(writeTO);
			close();
		} catch (ValidationException ve) {
			new ErrorDialog("Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage())
					.open();
		} catch (Exception ve) {
			new ErrorDialog("Uložení se nezdařilo").open();
		}
	}

	private void placeImage(BookTO to) {
		// https://vaadin.com/forum/thread/260778
		String name = to.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		image.setSrc(new StreamResource(name, () -> new ByteArrayInputStream(to.getImage())));
		image.setVisible(true);
		imageLayout.removeAll();
		imageLayout.add(image);
		imageLayout.setHorizontalComponentAlignment(Alignment.CENTER, image);

		DeleteButton deleteButton = new DeleteButton(e -> {
			to.setImage(null);
			placeUpload();
		});
		imageLayout.add(deleteButton);
		imageLayout.setHorizontalComponentAlignment(Alignment.CENTER, deleteButton);
	}

	private void placeUpload() {
		imageLayout.removeAll();
		imageLayout.add(upload);
		imageLayout.setHorizontalComponentAlignment(Alignment.CENTER, upload);
	}

	protected VerticalLayout createForm(Binder<BookTO> binder) {
		TextField nameField = new TextField("Název");
		nameField.setWidth("600px");
		binder.forField(nameField).asRequired().bind(BookTO::getName, BookTO::setName);

		HorizontalLayout line1Layout = new HorizontalLayout(nameField);

		TextField authorField = new TextField("Autor");
		authorField.setWidth("200px");
		binder.forField(authorField).asRequired().bind(BookTO::getAuthor, BookTO::setAuthor);

		TextField releasedField = new TextField("Vydáno");
		binder.forField(releasedField).bind(BookTO::getYear, BookTO::setYear);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(BookTO::getRating, BookTO::setRating);

		HorizontalLayout line2Layout = new HorizontalLayout(authorField, releasedField, ratingStars);
		ratingStars.getElement().getStyle().set("padding-bottom", "10px");
		line2Layout.setVerticalComponentAlignment(Alignment.END, ratingStars);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(BookTO::getDescription, BookTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");

		return new VerticalLayout(line1Layout, line2Layout, descriptionField);
	}

}

package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass.drinks.util.ImageUtils;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.components.button.CreateButton;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import cz.gattserver.grass.core.ui.components.button.ModifyButton;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class DrinkDialog<T extends DrinkTO> extends EditWebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	private static final Logger logger = LoggerFactory.getLogger(DrinkDialog.class);

	private VerticalLayout imageLayout;
	private Upload upload;
	private Image image;

	public DrinkDialog() {
		this(null);
	}

	public DrinkDialog(final T originalTO) {
		T formTO = createNewInstance();

		Binder<T> binder = new Binder<>();
		binder.setBean(formTO);

		setWidth("800px");

		imageLayout = new VerticalLayout();
		imageLayout.setPadding(false);
		imageLayout.setWidth(null);
		addComponent(imageLayout);

		// musí tady něco být nahrané, jinak to pak nejde měnit (WTF?!)
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

		VerticalLayout rightPartLayout = new VerticalLayout();
		rightPartLayout.setPadding(false);

		FormLayout fieldsLayout = createForm(binder);
		rightPartLayout.add(fieldsLayout);

		HorizontalLayout btnsLayout = new HorizontalLayout();
		btnsLayout.setSpacing(false);
		btnsLayout.setPadding(false);
		btnsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		btnsLayout.setWidthFull();
		rightPartLayout.add(btnsLayout);

		if (originalTO != null)
			btnsLayout.add(new ModifyButton(event -> save(originalTO, binder)));
		else
			btnsLayout.add(new CreateButton(event -> save(originalTO, binder)));

		btnsLayout.add(new CloseButton(e -> close()));

		HorizontalLayout mainLayout = new HorizontalLayout(imageLayout, rightPartLayout);
		mainLayout.expand(rightPartLayout);
		mainLayout.setPadding(false);
		addComponent(mainLayout);

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	private void save(T originalTO, Binder<T> binder) {
		try {
			T writeTO = originalTO == null ? createNewInstance() : originalTO;
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

	private void placeImage(T to) {
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

	protected abstract void onSave(T to);

	protected abstract T createNewInstance();

	protected abstract FormLayout createForm(Binder<T> binder);

}

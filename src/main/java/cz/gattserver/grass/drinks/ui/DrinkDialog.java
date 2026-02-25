package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.ImageUtils;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.drinks.model.interfaces.DrinkTO;
import cz.gattserver.grass.core.ui.util.UIUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Slf4j
public abstract class DrinkDialog<T extends DrinkTO> extends EditWebDialog {

    @Serial
    private static final long serialVersionUID = -5230507386391587351L;

    private final Consumer<T> onSave;

    private final VerticalLayout imageLayout;
    private final Upload upload;
    private final Image image;

    public DrinkDialog(Consumer<T> onSave) {
        this(null, onSave);
    }

    public DrinkDialog(final T originalTO, Consumer<T> onSave) {
        super("Nápoj");
        this.onSave = onSave;

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
        upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            try {
                // vytvoř miniaturu
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageUtils.resizeImageFile(metadata.fileName(), new FileInputStream(file), bos, 400, 400);
                formTO.setImage(bos.toByteArray());
                placeImage(formTO);
            } catch (IOException ex) {
                String err = "Nezdařilo se nahrát obrázek nápoje";
                log.error(err, ex);
                UIUtils.showError(err);
            }
        }));
        upload.setMaxFileSize(2000000);
        upload.setAcceptedFileTypes("image/jpg", "image/jpeg", "image/png");

        if (originalTO == null || originalTO.getImage() == null) placeUpload();
        else {
            placeImage(originalTO);
            formTO.setImage(originalTO.getImage());
        }

        VerticalLayout rightPartLayout = new VerticalLayout();
        rightPartLayout.setPadding(false);

        FormLayout fieldsLayout = createForm(binder);
        rightPartLayout.add(fieldsLayout);

        HorizontalLayout mainLayout = new HorizontalLayout(imageLayout, rightPartLayout);
        mainLayout.expand(rightPartLayout);
        mainLayout.setPadding(false);
        addComponent(mainLayout);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e->save(originalTO,binder), event -> close()));

        if (originalTO != null) binder.readBean(originalTO);
    }

    private void save(T originalTO, Binder<T> binder) {
        try {
            T writeTO = originalTO == null ? createNewInstance() : originalTO;
            binder.writeBean(writeTO);
            writeTO.setImage(binder.getBean().getImage());
            onSave.accept(writeTO);
            close();
        } catch (ValidationException ex) {
            // ValidationException je zpracována přes UI a zbytek chci, aby vyskočil do error dialogu
        }
    }

    private void placeImage(T to) {
        // https://vaadin.com/forum/thread/260778
        String name = to.getName() + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        image.setSrc(DownloadHandler.fromInputStream(
                e -> new DownloadResponse(new ByteArrayInputStream(to.getImage()), name, null, -1)));
        image.setVisible(true);
        imageLayout.removeAll();
        imageLayout.add(image);
        imageLayout.setHorizontalComponentAlignment(Alignment.CENTER, image);

        Button deleteButton = componentFactory.createDeleteButton(e -> {
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

    protected abstract T createNewInstance();

    protected abstract FormLayout createForm(Binder<T> binder);

}
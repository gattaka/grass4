package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PGUploadBuilder {

    private static final Logger logger = LoggerFactory.getLogger(PGUploadBuilder.class);

    private final Set<String> existingFiles;
    private final Set<UploadFile> uploadedFiles;

    public static class UploadFile {
        UploadMetadata metadata;
        File file;

        public UploadFile(UploadMetadata metadata, File file) {
            this.metadata = metadata;
            this.file = file;
        }
    }

    public Upload createUpload(Consumer<Set<UploadFile>> onAllUploaded, Supplier<Set<String>> galleryFilesSupplier) {
        Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            if (galleryFilesSupplier.get().contains(file.getName())) {
                existingFiles.add(file.getName());
            } else {
                uploadedFiles.add(new UploadFile(metadata, file));
            }
        }));
        upload.setAcceptedFileTypes("image/*", "video/*", ".xcf", ".ttf", ".otf");
        upload.addAllFinishedListener(event -> onDone(onAllUploaded));
        return upload;
    }

    public PGUploadBuilder() {
        existingFiles = new HashSet<>();
        uploadedFiles = new HashSet<>();
    }

    protected void onDone(Consumer<Set<UploadFile>> onAllUploaded) {
        if (existingFiles.isEmpty()) {
            onAllUploaded.accept(uploadedFiles);
        } else {
            WarnDialog warnWindow = new WarnDialog("Následující soubory již existují:") {
                private static final long serialVersionUID = 3428203680996794639L;

                @Override
                protected void createDetails(String details) {
                    HtmlDiv div = new HtmlDiv();
                    String value = "";
                    for (String existing : existingFiles)
                        value += existing + "<br/>";
                    div.setValue(value);
                    addComponent(div);
                }

                @Override
                public void close() {
                    super.close();
                    onAllUploaded.accept(uploadedFiles);
                }
            };
            ProgressDialog.runInUI(() -> warnWindow.open(), UI.getCurrent());
        }
    }
}
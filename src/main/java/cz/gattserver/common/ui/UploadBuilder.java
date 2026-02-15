package cz.gattserver.common.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.server.streams.UploadMetadata;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serial;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UploadBuilder {

    private static final Logger logger = LoggerFactory.getLogger(UploadBuilder.class);

    private final Set<String> refusedFiles;
    private final Set<UploadFile> uploadedFiles;

    public static class UploadFile {
        private UploadMetadata metadata;
        private File file;

        public UploadFile(UploadMetadata metadata, File file) {
            this.metadata = metadata;
            this.file = file;
        }

        public UploadMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(UploadMetadata metadata) {
            this.metadata = metadata;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    public Upload createUpload(Consumer<Set<UploadFile>> onAllUploaded, Supplier<Set<String>> existingFilesSupplier, String... acceptedFileTypes) {
        Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            if (existingFilesSupplier.get().contains(metadata.fileName())) {
                refusedFiles.add(metadata.fileName());
            } else {
                uploadedFiles.add(new UploadFile(metadata, file));
            }
        }));
        upload.setAcceptedFileTypes(acceptedFileTypes);
        upload.addAllFinishedListener(event -> onDone(onAllUploaded));
        return upload;
    }

    public UploadBuilder() {
        refusedFiles = new HashSet<>();
        uploadedFiles = new HashSet<>();
    }

    protected void onDone(Consumer<Set<UploadFile>> onAllUploaded) {
        if (refusedFiles.isEmpty()) {
            onAllUploaded.accept(uploadedFiles);
            uploadedFiles.clear();
            refusedFiles.clear();
        } else {
            WarnDialog warnWindow = new WarnDialog("Následující soubory již existují:") {

                @Serial
                private static final long serialVersionUID = 1180457720250214929L;

                @Override
                protected void createDetails(String details) {
                    HtmlDiv div = new HtmlDiv();
                    String value = "";
                    for (String existing : refusedFiles)
                        value += existing + "<br/>";
                    div.setValue(value);
                    addComponent(div);
                }

                @Override
                public void close() {
                    super.close();
                    onAllUploaded.accept(uploadedFiles);
                    uploadedFiles.clear();
                    refusedFiles.clear();
                }
            };
            ProgressDialog.runInUI(() -> warnWindow.open(), UI.getCurrent());
        }
    }
}
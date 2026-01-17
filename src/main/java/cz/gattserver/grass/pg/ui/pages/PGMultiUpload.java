package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.upload.Upload;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;

public class PGMultiUpload extends Upload {

	private static final long serialVersionUID = -5223991901495532219L;

	private static final Logger logger = LoggerFactory.getLogger(PGMultiUpload.class);

	@Autowired
	private PGService pgService;

	private GrassMultiFileBuffer buffer;
	private Set<String> existingFiles;
	private boolean allUploadsProcessed;

	public PGMultiUpload(String galleryDir) {
		existingFiles = new HashSet<>();
		allUploadsProcessed = false;
		setAcceptedFileTypes("image/*", "video/*", ".xcf", ".ttf", ".otf");
		buffer = new GrassMultiFileBuffer();
		setReceiver(buffer);
		SpringContextHelper.inject(this);

		// https://github.com/vaadin/vaadin-upload-flow/issues/134
		getElement().addEventListener("upload-start", e -> this.allUploadsProcessed = false);
		getElement().addEventListener("upload-success", e -> {
			JsonNode files = e.getEventData().get("element.files");

			boolean allUploadsProcessed = true;
			for (JsonNode file : files)
				if (!file.get("complete").asBoolean())
					allUploadsProcessed = false;

			if (!this.allUploadsProcessed && allUploadsProcessed)
				onDone();
			this.allUploadsProcessed = allUploadsProcessed;
		}).addEventData("element.files");

		addFinishedListener(event -> {
			try {
				pgService.uploadFile(buffer.getInputStream(event.getFileName()), event.getFileName(), galleryDir);
				fileUploadSuccess(event.getFileName());
			} catch (FileAlreadyExistsException f) {
				existingFiles.add(event.getFileName());
			} catch (IOException e) {
				logger.error("Nezdařilo se uložit soubor {}", event.getFileName(), e);
			}
		});
	}

	protected void onDone() {
		if (existingFiles.isEmpty()) {
			allFilesUploaded();
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
					allFilesUploaded();
				}
			};
			ProgressDialog.runInUI(() -> warnWindow.open(), UI.getCurrent());
		}
	}

	protected void fileUploadSuccess(String fileName) {
	}

	protected void allFilesUploaded() {
	}

}
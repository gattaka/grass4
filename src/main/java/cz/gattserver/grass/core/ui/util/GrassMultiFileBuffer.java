package cz.gattserver.grass.core.ui.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.receivers.FileData;

/**
 * credit:
 * 
 * https://github.com/vaadin/vaadin-upload-flow/issues/105
 *
 */
@Deprecated
public class GrassMultiFileBuffer implements MultiFileReceiver {

	private static final Logger logger = LoggerFactory.getLogger(GrassMultiFileBuffer.class);

	private Map<String, FileData> files = new HashMap<>();

	private Map<String, String> tempFileNames = new HashMap<>();

	private FileOutputStream createFileOutputStream(String fileName) {
		try {
			return new FileOutputStream(createFile(fileName));
		} catch (IOException e) {
			logger.warn("Failed to create file output stream for: '" + fileName + "'", e);
		}
		return null;
	}

	private File createFile(String fileName) throws IOException {
		String tempFileName = "upload_tmpfile_" + fileName + "_" + System.currentTimeMillis();

		File tempFile = File.createTempFile(tempFileName, null);
		tempFileNames.put(fileName, tempFile.getPath());

		return tempFile;
	}

	@Override
	public OutputStream receiveUpload(String fileName, String MIMEType) {
		FileOutputStream outputBuffer = createFileOutputStream(fileName);
		files.put(fileName, new FileData(fileName, MIMEType, outputBuffer));

		return outputBuffer;
	}

	public Set<String> getFiles() {
		return files.keySet();
	}

	public FileData getFileData(String fileName) {
		return files.get(fileName);
	}

	public InputStream getInputStream(String fileName) {
		if (tempFileNames.containsKey(fileName)) {
			try {
				return new FileInputStream(tempFileNames.get(fileName));
			} catch (IOException e) {
				logger.warn("Failed to create InputStream for: '" + fileName + "'", e);
			}
		}
		return new ByteArrayInputStream(new byte[0]);
	}

}
package cz.gattserver.grass.pg.util;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class DecodeAndCaptureFrames {

	public BufferedImage decodeAndCaptureFrames(Path file) throws IOException, JCodecException {
		try (FileChannel fc = FileChannel.open(file)) {
			Picture picture = FrameGrab.getFrameFromChannel(new FileChannelWrapper(fc), 1);
			return AWTUtil.toBufferedImage(picture);
		}
	}

}
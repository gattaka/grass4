package cz.gattserver.grass.pg.interfaces;

import java.nio.file.Path;
import java.util.Date;

public class PGSettingsItemTO implements Comparable<PGSettingsItemTO> {

	private Path path;
	private PhotogalleryRESTOverviewTO overviewTO;
	private Long size;
	private Long filesCount;
	private Date date;

	public PGSettingsItemTO(Path path, PhotogalleryRESTOverviewTO overviewTO, Long size, Long filesCount, Date date) {
		this.path = path;
		this.overviewTO = overviewTO;
		this.size = size;
		this.filesCount = filesCount;
		this.date = date;
	}

	public Long getFilesCount() {
		return filesCount;
	}

	public void setFilesCount(Long filesCount) {
		this.filesCount = filesCount;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public PhotogalleryRESTOverviewTO getOverviewTO() {
		return overviewTO;
	}

	public void setOverviewTO(PhotogalleryRESTOverviewTO overviewTO) {
		this.overviewTO = overviewTO;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int compareTo(PGSettingsItemTO o) {
		return path.getFileName().toString().compareTo(o.getPath().getFileName().toString());
	}

}

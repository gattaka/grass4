package cz.gattserver.grass.medic.interfaces;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class ReportFileTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 648049593899110826L;

	private String name;
	private String humanSize;
	private String mime;
	private long size;
	private byte[] content;
	private LocalDateTime lastModified;

	public ReportFileTO(String name, String mime, long size, byte[] content) {
		this.name = name;
		this.mime = mime;
		this.size = size;
		this.humanSize = HumanBytesSizeFormatter.format(content.length);
		this.content = content;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ReportFileTO that))
			return false;
		return Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName());
	}
}
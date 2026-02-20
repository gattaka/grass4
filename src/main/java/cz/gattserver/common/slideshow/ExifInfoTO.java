package cz.gattserver.common.slideshow;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExifInfoTO {

	private LocalDateTime date;
	private Long dateMillis;
	private Integer orinetation;
	private String deviceMaker;
	private String deviceModel;
	private Double latitude;
	private Double longitude;

}
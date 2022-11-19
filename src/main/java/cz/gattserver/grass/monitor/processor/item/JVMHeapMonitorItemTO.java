package cz.gattserver.grass.monitor.processor.item;

import java.util.ArrayList;
import java.util.List;

public class JVMHeapMonitorItemTO extends MonitorItemTO {

	public static class Line {
		private int num;
		private int instances;
		private int bytes;
		private String name;

		public Line(int num, int instances, int bytes, String name) {
			super();
			this.num = num;
			this.instances = instances;
			this.bytes = bytes;
			this.name = name;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public int getInstances() {
			return instances;
		}

		public void setInstances(int instances) {
			this.instances = instances;
		}

		public int getBytes() {
			return bytes;
		}

		public void setBytes(int bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	// debug
	private String fileName;

	private List<Line> lines = new ArrayList<>();

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

}

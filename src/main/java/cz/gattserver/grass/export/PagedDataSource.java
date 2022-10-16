package cz.gattserver.grass.export;

import java.util.List;

public abstract class PagedDataSource<T> {

	private static final int DEFAULT_PAGE_SIZE = 50000;

	private final int size;
	private int index = -1;
	private List<T> pageData = null;

	private int pageSize;

	protected abstract List<T> getData(int page, int size);

	protected abstract void indicateProgress();

	public PagedDataSource(int dataSize, int pageSize) {
		this.size = dataSize;
		this.pageSize = pageSize;
	}

	public PagedDataSource(int dataSize) {
		this(dataSize, PagedDataSource.DEFAULT_PAGE_SIZE);
	}

	public int getSize() {
		return size;
	}

	public boolean next() {
		index++;
		boolean hasNext = (index < size) && (size != 0);

		// pokud existuje další řádek a pokud je na další stránce,
		// nahraj stránku
		if (hasNext && index % pageSize == 0) {
			pageData = getData(index / pageSize, pageSize);
		}

		indicateProgress();

		return hasNext;
	}

	public T getLineItem() {
		return pageData == null ? null : pageData.get(index % pageSize);
	}

};

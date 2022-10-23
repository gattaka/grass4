package cz.gattserver.grass.core.export;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class JasperExportDataSource<T> implements JRDataSource {

	private static Logger logger = LoggerFactory.getLogger(JasperExportDataSource.class);

	private PagedDataSource<T> dataSource;

	public JasperExportDataSource(PagedDataSource<T> dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean next() throws JRException {
		return dataSource.next();
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		T lineItem = dataSource.getLineItem();
		Object[] args = {};
		Class<?> lineItemClass = lineItem.getClass();
		for (Method m : lineItemClass.getMethods()) {
			if (m.getName().toLowerCase().endsWith(jrField.getName().toLowerCase())) {
				int prefixLength = 0;
				if (m.getName().startsWith("is")) {
					prefixLength = 2;
				}
				if (m.getName().startsWith("get")) {
					prefixLength = 3;
				}
				if ((prefixLength != 0) && (m.getName().length() == prefixLength + jrField.getName().length())) {
					try {
						return m.invoke(lineItem, args);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						logger.error("Chyba při vytváření Jasper reportu", e);
						throw new JRException("Getter invocation failed");
					}
				}
			}
		}
		String errMsg = "Getter lookup failed for field: " + jrField.getName() + " on class: " + lineItemClass;
		logger.error(errMsg);
		throw new JRException(errMsg);
	}
}
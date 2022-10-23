package cz.gattserver.grass.core.ui.util;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DoubleToIntegerConverter implements Converter<Double, Integer> {

	private static final long serialVersionUID = -1344832908495652879L;

	@Override
	public Result<Integer> convertToModel(Double value, ValueContext context) {
		if (value == null)
			return Result.ok(null);
		return Result.ok(value.intValue());
	}

	@Override
	public Double convertToPresentation(Integer value, ValueContext context) {
		if (value == null)
			return null;
		return value.doubleValue();
	}

}

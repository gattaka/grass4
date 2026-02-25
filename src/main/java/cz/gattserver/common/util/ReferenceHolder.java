package cz.gattserver.common.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public final class ReferenceHolder<T> {

	private T value;

}
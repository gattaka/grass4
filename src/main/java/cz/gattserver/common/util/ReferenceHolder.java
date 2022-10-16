package cz.gattserver.common.util;

/**
 * Primitivní typy jako (B/b)oolean se nedají předávat do anonymních tříd,
 * protože (většinou) nemůžou být final
 */
public final class ReferenceHolder<T> {

	private T value;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public static ReferenceHolder<Boolean> newBooleanHolder() {
		return new ReferenceHolder<>();
	}

	public static ReferenceHolder<Boolean> newBooleanHolder(Boolean value) {
		ReferenceHolder<Boolean> holder = ReferenceHolder.newBooleanHolder();
		holder.setValue(value);
		return holder;
	}

	public static ReferenceHolder<Integer> newIntegerHolder() {
		return new ReferenceHolder<>();
	}

	public static ReferenceHolder<Integer> newIntegerHolder(Integer value) {
		ReferenceHolder<Integer> holder = ReferenceHolder.newIntegerHolder();
		holder.setValue(value);
		return holder;
	}

}

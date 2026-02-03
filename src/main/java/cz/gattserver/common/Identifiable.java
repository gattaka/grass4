package cz.gattserver.common;

public interface Identifiable<T> {

    T getId();

    void setId(T id);

}
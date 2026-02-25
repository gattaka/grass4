package cz.gattserver.grass.drinks.model.domain;

import lombok.Getter;

@Getter
public enum WineType {

    RED("Červené"), WHITE("Bílé"), ROSE("Růžové"), FRUIT("Ovocné"), MEAD("Medové");

    private final String caption;

    WineType(String caption) {
        this.caption = caption;
    }

}
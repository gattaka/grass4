package cz.gattserver.grass.drinks.model.domain;

import lombok.Getter;

@Getter
public enum DrinkType {

    BEER("Pivo"), WINE("Víno"), RUM("Rum"), WHISKY("Whisky"), OTHER("Jiné");

    private final String caption;

    DrinkType(String caption) {
        this.caption = caption;
    }

}
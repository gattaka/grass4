package cz.gattserver.grass.books.model.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class BookFilterTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7182047636435578157L;

    private String name;
    private String author;
}
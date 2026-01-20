package cz.gattserver.grass.recipes.model.domain;

import jakarta.persistence.*;

@Entity(name = "RECIPE")
public class Recipe {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Název receptu
     */
    private String name;

    /**
     * Popis receptu
     */
    @Column(columnDefinition = "TEXT")
    private String description;


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Recipe)) return false;
        return ((Recipe) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

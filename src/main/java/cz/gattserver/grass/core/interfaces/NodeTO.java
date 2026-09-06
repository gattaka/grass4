package cz.gattserver.grass.core.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class NodeTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4541625019058100744L;

    /**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název uzlu
	 */
	private String name;

	/**
	 * Předek uzlu
	 */
	private String parentName;
	private Long parentId;

    /**
     * Je uzel skrytý?
     */
    private Boolean hidden = false;

    /**
     * Je uzel skrytý dle jeho předka?
     */
    private Boolean hiddenByParent = false;

    @QueryProjection
    public NodeTO(Long id, String name, String parentName, Long parentId, Boolean hidden,
                  Boolean hiddenByParent) {
        this.id = id;
        this.name = name;
        this.parentName = parentName;
        this.parentId = parentId;
        this.hidden = hidden;
        this.hiddenByParent = hiddenByParent;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof NodeTO nodeTO)) return false;
        return Objects.equals(id, nodeTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
package cz.gattserver.grass.articles.model;

import cz.gattserver.grass.core.model.domain.ContentNode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SortComparator;

import jakarta.persistence.*;
import java.util.Set;
import java.util.SortedSet;

@Setter
@Getter
@Entity(name = "ARTICLE")
public class Article {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	/**
	 * Obsah článku
	 */
	@Column(columnDefinition = "TEXT")
	private String text;

	/**
	 * Přeložený obsah článku
	 */
	@Column(columnDefinition = "TEXT")
	private String outputHTML;

	/**
	 * Obsah článku upravený pro vyhledávání
	 */
	@Column(columnDefinition = "TEXT")
	private String searchableOutput;

	/**
	 * Meta-informace o obsahu
	 */
    @Column(name = "CONTENTNODE_ID")
    private Long contentNodeId;

	/**
	 * Id adresáře příloh
	 */
	private String attachmentsDirId;

}
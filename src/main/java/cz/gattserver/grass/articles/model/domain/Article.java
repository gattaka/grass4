package cz.gattserver.grass.articles.model.domain;

import cz.gattserver.grass.core.model.domain.ContentNode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NativeGenerator;
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
	@OneToOne
	private ContentNode contentNode;

	/**
	 * Dodatečné CSS resources, které je potřeba nahrát (od pluginů)
	 */
	@ElementCollection
	@CollectionTable(name = "ARTICLE_CSS_RESOURCES")
	@Column(name = "pluginCSSResources")
	private Set<String> pluginCSSResources;

	/**
	 * Dodatečné JS resources, které je potřeba nahrát (od pluginů)
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@SortComparator(ArticleJSResourceComparator.class)
	private SortedSet<ArticleJSResource> pluginJSResources;

	/**
	 * Dodatečné JS kódy, které je potřeba nahrát (JS z článků)
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@SortComparator(ArticleJSCodeComparator.class)
	private SortedSet<ArticleJSCode> pluginJSCodes;

	/**
	 * Id adresáře příloh
	 */
	private String attachmentsDirId;

}
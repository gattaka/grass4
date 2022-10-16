package cz.gattserver.grass.ui.components;

import com.vaadin.flow.component.html.Anchor;

/**
 * Speciální úprava Vaadin link elementu pro tabulky, které potřebují, aby byl
 * typ v daném sloupci {@link Comparable} jinak sloupec nejde řadit (nelze řadit
 * podle něj) - v případě linku by přitom stačilo porovnávat jenom název.
 * 
 * @author Gattaka
 * 
 */
public class ComparableLink extends Anchor implements Comparable<ComparableLink> {

	private static final long serialVersionUID = -1066469018592736445L;

	public ComparableLink(String name, String url) {
		super(name, url);
	}

	public int compareTo(ComparableLink o) {
		return getText().compareTo(o.getText());
	}

}

package cz.gattserver.grass.articles.plugins.favlink.plugin;


import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FavlinkElementTest {

	@Test
	public void test() {
		FavlinkElement element = new FavlinkElement("mock/imgs/favicon/fav.ico", null, "http://mock.url.website");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals(
				"<a style=\"word-wrap: break-word; white-space: nowrap\" href=\"http://mock.url.website\" >"
						+ "<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"mock/imgs/favicon/fav.ico\" />http://mock.url.website</a>",
				context.getOutput());
	}

	@Test
	public void testEmpty() {
		FavlinkElement element = new FavlinkElement(null, null, "http://mock.url.website");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals("<a style=\"word-wrap: break-word; white-space: nowrap\" href=\"http://mock.url.website\" >http://mock.url.website</a>",
				context.getOutput());
	}

	@Test
	public void testLong() {
		FavlinkElement element = new FavlinkElement("mock/imgs/favicon/fav.ico", null,
				"https://cs.wikibooks.org/wiki/Praktick%C3%A1_elektronika/Spektrum_sign%C3%A1lu#Obd%C3%A9ln%C3%ADkov%C3%BD_sign%C3%A1l");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals(
				"<a style=\"word-wrap: break-word; white-space: nowrap\" href=\"https://cs.wikibooks.org/wiki/Praktick%C3%A1_elektronika/Spektrum_sign%C3%A1lu#Obd%C3%A9ln%C3%ADkov%C3%BD_sign%C3%A1l\" >"
						+ "<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"mock/imgs/favicon/fav.ico\" />https://cs.wikibooks.org/wiki/Praktick%C3%A1_el...ign%C3%A1lu#Obd%C3%A9ln%C3%ADkov%C3%BD_sign%C3%A1l</a>",
				context.getOutput());
	}

	@Test
	public void testWithDescription() {
		FavlinkElement element = new FavlinkElement("mock/imgs/favicon/fav.ico", "popis", "http://mock.url.website");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals(
				"popis <a style=\"word-wrap: break-word; white-space: nowrap\" href=\"http://mock.url.website\" title=\"popis\" >"
						+ "<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"mock/imgs/favicon/fav.ico\" />http://mock.url.website</a>",
				context.getOutput());
	}

	@Test
	public void testWithLongDescription() {
		FavlinkElement element = new FavlinkElement("mock/imgs/favicon/fav.ico",
				"Ještě delší text, který se sem už vůbec nemá šanci vejít protože je moc dlouhej a tak ho systém bude muset zkrátit aby se sem vešel",
				"http://mock.url.website");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals(
				"Ještě delší text, který se sem už vůbec nemá šanci vejít protože je moc dlouh... <a style=\"word-wrap: break-word; white-space: nowrap\" href=\"http://mock.url.website\" title=\"Ještě delší text, který se sem už vůbec nemá šanci vejít protože je moc dlouhej a tak ho systém bude muset zkrátit aby se sem vešel\" >"
						+ "<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"mock/imgs/favicon/fav.ico\" />http://...rl.website</a>",
				context.getOutput());
	}

	@Test
	public void testLongWithLongDescription() {
		FavlinkElement element = new FavlinkElement("mock/imgs/favicon/fav.ico",
				"Ještě delší text, který se sem už vůbec nemá šanci vejít protože je moc dlouhej a tak ho systém bude muset zkrátit aby se sem vešel",
				"http://mock.url.website/questions/3357477/is-asynctask-really-conceptually-flawed-or-am-i-just-missing-something/3359003#3359003");
		Context context = new ContextImpl();
		element.apply(context);
		assertEquals(
				"Ještě delší text, který se sem už vůbec nemá šanci vejít protože je moc dlouh... "
						+ "<a style=\"word-wrap: break-word; white-space: nowrap\" href=\"http://mock.url.website/questions/3357477/is-asynctask-really-conceptually-flawed-or-am-i-just-missing-something/3359003#3359003\" title=\"Ještě delší text, který se sem už vůbec nemá šanci vejít protože je moc dlouhej a tak ho systém bude muset zkrátit aby se sem vešel\" >"
						+ "<img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"mock/imgs/favicon/fav.ico\" />http://...03#3359003</a>",
				context.getOutput());
	}

}

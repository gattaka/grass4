package cz.gattserver.web.common.server;

import cz.gattserver.common.server.URLPathAnalyzer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class URLPathAnalyzerTest {

	@Test
	public void testEmpty() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("");
		assertTrue(analyzer.isEmpty());
	}

	@Test
	public void testEmpty2() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("/");
		assertTrue(analyzer.isEmpty());
	}

	@Test
	public void testGetCurrentPathToken() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("/articles-editor/new/112-Testing");
		assertEquals("articles-editor", analyzer.getCurrentPathToken());
	}

	@Test
	public void testGetCurrentPathToken2() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("articles-editor/new/112-Testing");
		assertEquals("articles-editor", analyzer.getCurrentPathToken());
	}

	@Test
	public void testGetNextPathToken() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("/articles-editor/new/112-Testing");
		assertEquals("articles-editor", analyzer.getNextPathToken());
		assertEquals("new", analyzer.getNextPathToken());
		assertEquals("112-Testing", analyzer.getNextPathToken());
		assertNull(analyzer.getNextPathToken());
		assertNull(analyzer.getNextPathToken());
	}

	@Test
	public void testShift() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("/articles-editor/new/112-Testing");
		assertEquals("articles-editor", analyzer.getCurrentPathToken());
		assertEquals("articles-editor", analyzer.getCurrentPathToken());
		analyzer.shift();
		assertEquals("new", analyzer.getCurrentPathToken());
	}

	@Test
	public void testGetNextPathToken2() {
		URLPathAnalyzer analyzer = new URLPathAnalyzer("articles-editor/new/112-Testing");
		assertEquals("articles-editor", analyzer.getNextPathToken());
		assertEquals("new", analyzer.getNextPathToken());
		assertEquals("112-Testing", analyzer.getNextPathToken());
		assertNull(analyzer.getNextPathToken());
		assertNull(analyzer.getNextPathToken());
	}

}

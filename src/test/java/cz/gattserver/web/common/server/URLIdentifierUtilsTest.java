package cz.gattserver.web.common.server;

import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.server.URLIdentifierUtils.URLIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class URLIdentifierUtilsTest {

	@Test
	public void testCreateURLIdentifier_basic() {
		String identifier = URLIdentifierUtils.createURLIdentifier(45L, "test");
		assertEquals("45-test", identifier);
	}

	@Test
	public void testCreateURLIdentifier_special() {
		String identifier = URLIdentifierUtils.createURLIdentifier(45L, "test WTF/min");
		assertEquals("45-test-wtf-min", identifier);
	}

	@Test
	public void testCreateURLIdentifier_unicode() {
		String identifier = URLIdentifierUtils.createURLIdentifier(45L,
				"ó překontně tést češkých ďábelských měkkých znáčků a žlúťoučkých žíraf Ň!");
		assertEquals("45-o-prekontne-test-ceskych-dabelskych-mekkych-znacku-a-zlutouckych-ziraf-n-", identifier);
	}

	@Test
	public void testParseURLIdentifier_basic() {
		URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier("45-test");
		assertEquals(new Long(45L), identifier.getId());
		assertEquals("test", identifier.getName());
	}

	@Test
	public void testParseURLIdentifier_special() {
		URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier("45-test+WTF%252Fmin");
		assertEquals(new Long(45L), identifier.getId());
		assertEquals("test WTF%2Fmin", identifier.getName());
	}

	@Test
	public void testParseURLIdentifier_unicode() {
		URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(
				"45-test+%C4%8Desk%C3%BDch+zn%C3%A1%C4%8Dk%C5%AF+a+%C5%BElu%C5%A5ou%C4%8Dk%C3%BDch+%C5%BEiraf+%C5%87%21");
		assertEquals(new Long(45L), identifier.getId());
		assertEquals("test českých znáčků a žluťoučkých žiraf Ň!", identifier.getName());
	}

	@Test
	public void testParseURLIdentifier_null() {
		URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(null);
		assertNull(identifier);
	}

	@Test
	public void testParseURLIdentifier_wrong() {
		URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier("45646");
		assertNull(identifier);
	}

	@Test
	public void testParseURLIdentifier_wrong2() {
		URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier("WXXx-45646");
		assertNull(identifier);
	}

}

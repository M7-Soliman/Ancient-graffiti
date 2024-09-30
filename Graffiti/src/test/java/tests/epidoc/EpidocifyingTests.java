/**
 * 
 */
package tests.epidoc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc;

/**
 * Setting up to test transforming content to EpiDoc.
 * 
 * TODO: this needs to be firmed up more.
 * 
 * @author sprenkles
 *
 */
class EpidocifyingTests {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link edu.wlu.graffiti.data.setup.TransformEDRContentToEpiDoc#transformContentToEpiDoc(java.lang.String)}.
	 */
	@Test
	void testTransformContentToEpiDoc() {
		
		// Trying to make tests based on http://ancientgraffiti.org/Graffiti/graffito/AGP-EDR167837
		// it is throwing errors. 
		// Need to break it down into smaller pieces and make sure those work, then try the whole inscription.

		assertEquals(
				"<lb n='1'/><gap reason='illegible' quantity='1' unit='character'/><unclear>N</unclear><gap reason='illegible' quantity='1' unit='character'/><orig>dvs</orig>",
				TransformEDRContentToEpiDoc.transformContentToEpiDoc("+Ṇ+DVS"));

		// I am not sure about this test--> what it's supposed to be.
		assertEquals("<lb n='1'/>"
				+ "<gap reason='illegible' quantity='1' unit='character'/><supplied reason='undefined' evidence='previouseditor'></supplied>",
				TransformEDRContentToEpiDoc.transformContentToEpiDoc("+̲"));

	}

}

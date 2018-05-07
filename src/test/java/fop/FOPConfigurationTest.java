package fop;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

class FOPConfigurationTest {

	@Test
	void simpleFop() throws IOException, SAXException, TransformerException {
		FOPProducer fopProducer = new FOPProducer(FOPConfiguration.getInstance(), "<?xml version=\"1.0\"?>\n" +
				"<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n" +
				"  <fo:layout-master-set>\n" +
				"    <fo:simple-page-master master-name=\"content\"\n" +
				"        page-width=\"210mm\" page-height=\"297mm\">\n" +
				"      <fo:region-body/>\n" +
				"    </fo:simple-page-master>\n" +
				"  </fo:layout-master-set>\n" +
				"  <fo:page-sequence master-reference=\"content\">\n" +
				"    <fo:flow flow-name=\"xsl-region-body\">\n" +
				"      <fo:table table-layout=\"fixed\" width=\"100%\">\n" +
				"        <fo:table-column column-width=\"proportional-column-width(1)\"/>\n" +
				"        <fo:table-body>\n" +
				"          <fo:table-row height=\"297mm\">\n" +
				"            <fo:table-cell display-align=\"center\">\n" +
				"              <fo:block text-align=\"center\">\n" +
				"                Hello World!\n" +
				"              </fo:block>\n" +
				"            </fo:table-cell>\n" +
				"          </fo:table-row>\n" +
				"        </fo:table-body>\n" +
				"      </fo:table>\n" +
				"    </fo:flow>\n" +
				"  </fo:page-sequence>\n" +
				"</fo:root>");

		fopProducer.transform();
		fopProducer.clean();
	}

	@Test
	void foFile() throws IOException, SAXException, TransformerException, URISyntaxException {
		FOPProducer fopProducer = new FOPProducer(
				FOPConfiguration.getInstance(),
				new File(getClass().getResource("simple.fo").toURI())
		);

		fopProducer.transform();
		fopProducer.clean();
	}
}
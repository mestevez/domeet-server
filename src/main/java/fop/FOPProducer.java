package fop;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import util.Path;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class FOPProducer {

	private FopFactory	fopFactory;
	private File foFile;
	private File pdfFile;
	private boolean isTemporaryFile;

	public FOPProducer(FopFactory fopFactory, File foFile) {
		this.fopFactory = fopFactory;
		this.foFile = foFile;
		this.isTemporaryFile = false;
	}

	public FOPProducer(FopFactory fopFactory, String fo) throws IOException {
		this(fopFactory, new File(Path.getTempPathFile("fop", "fo")));
		this.isTemporaryFile = true;

		Writer out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(this.foFile)), "UTF-8");
		try {
			out.write(fo);
		} finally {
			out.close();
		}
	}

	public void clean() {
		if (this.isTemporaryFile)
			foFile.delete();
		this.pdfFile.delete();
	}

	public File getPDF() {
		return pdfFile;
	}

	public void transform() throws IOException, FOPException, TransformerException {
		this.pdfFile = new File(
				Path.getTempPathFile("fop", foFile.getName().replaceAll("\\.[^.]+$", ""),
						"pdf"));

		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
		OutputStream out = new BufferedOutputStream(new FileOutputStream(this.pdfFile));
		try {
			// Step 3: Construct fop with desired output format
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			// Step 4: Setup JAXP using identity transformer
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(); // identity transformer

			// Step 5: Setup input and output for XSLT transformation
			// Setup input stream
			Source src = new StreamSource(foFile);

			// Resulting SAX events (the generated FO) must be piped through to FOP
			Result res = new SAXResult(fop.getDefaultHandler());

			// Step 6: Start XSLT transformation and FOP processing
			transformer.transform(src, res);

		} finally {
			//Clean-up
			out.close();
		}
	}
}

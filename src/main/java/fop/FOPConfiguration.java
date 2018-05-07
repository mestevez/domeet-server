package fop;

import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;
import util.Path;

import java.io.File;
import java.io.IOException;

public class FOPConfiguration {

	private static FopFactory FOPCONF;

	public static FopFactory getInstance() throws IOException, SAXException {
		if (FOPCONF == null) {
			FOPCONF = FopFactory.newInstance();

			FOPCONF.setUserConfig(new File(Path.getConfPath("fop-config.xml")));
		}

		return FOPCONF;
	}
}

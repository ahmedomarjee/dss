package eu.europa.esig.jaxb.xades;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.junit.Test;
import org.xml.sax.SAXException;

import eu.europa.esig.jaxb.xmldsig.SignatureType;

public class XAdESUtilsTest {

	@Test
	@SuppressWarnings("unchecked")
	public void test() throws JAXBException, SAXException, IOException {

		File xmldsigFile = new File("src/test/resources/xades-lta.xml");

		JAXBContext jc = XAdESUtils.getJAXBContext();
		assertNotNull(jc);

		Schema schema = XAdESUtils.getSchema();
		assertNotNull(schema);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setSchema(schema);

		JAXBElement<SignatureType> unmarshalled = (JAXBElement<SignatureType>) unmarshaller.unmarshal(xmldsigFile);
		assertNotNull(unmarshalled);

		Marshaller marshaller = jc.createMarshaller();
		marshaller.setSchema(schema);

		StringWriter sw = new StringWriter();
		marshaller.marshal(unmarshalled, sw);

		String xadesString = sw.toString();

		JAXBElement<SignatureType> unmarshalled2 = (JAXBElement<SignatureType>) unmarshaller.unmarshal(new StringReader(xadesString));
		assertNotNull(unmarshalled2);
	}

	@Test
	public void getSchema() throws SAXException, IOException {
		assertNotNull(XAdESUtils.getSchema());
		// cached
		assertNotNull(XAdESUtils.getSchema());
	}

}

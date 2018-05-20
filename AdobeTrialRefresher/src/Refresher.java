import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Refresher {

	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {

		File adobeDirectory = new File(System.getenv("programFiles") + "/Adobe");
		
		if(!adobeDirectory.exists()||!adobeDirectory.isDirectory()) {
			throw new RuntimeException("/Program Files/Adobe/ directory not found!");
		}
		
		for(File f : adobeDirectory.listFiles()) {
			if(f.isDirectory()) {
				File applicationFile = recursiveFileSearch(f,"application.xml");
				
				if(applicationFile != null) {
					incrementTrialSerialNumber(applicationFile);
				}
		
			}
		}
	}
	
	
	public static File recursiveFileSearch(File startFile, String fileName) {
		
		if(startFile.isFile()) {
			if(startFile.getName().equals(fileName)) {
				return startFile;
			}
		}
		if(startFile.isDirectory()) {

			for(File f : startFile.listFiles()) {
				if(recursiveFileSearch(f, fileName) != null) {
					return recursiveFileSearch(f, fileName);
				}
			}
		}
			
		return null;
	}
	
	
	public static void incrementTrialSerialNumber(File applicationFile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
		docbf.setNamespaceAware(true);
		DocumentBuilder docBuilder = docbf.newDocumentBuilder();
		Document doc = docBuilder.parse(applicationFile);
		
		NodeList nodeList = doc.getElementsByTagName("Data");
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node curr = nodeList.item(i);
			if(curr.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) curr;
				
				if(elem.getAttribute("key").equals("TrialSerialNumber")) {
					String trialSerialNumberString = elem.getFirstChild().getTextContent();
					BigInteger trialSerialNumber = new BigInteger(trialSerialNumberString);
					trialSerialNumber = trialSerialNumber.add(BigInteger.ONE);
					elem.setTextContent(trialSerialNumber.toString());
					break;
				}
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(applicationFile);
		transformer.transform(source, result);
		
		
	}
	
	
}


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Converts a given XML (with a line-element) into a csv (using Apache Commons CSV Library and SAX Parser as XML-Library)
 *
 * {@code
 * 
 * Sample XML: 
 * <T40010 xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance">
 *	<Satz>
 *	 <WAREN_NOM_BESCHR_SID>30963</WAREN_NOM_BESCHR_SID>
 *	 <WAREN_NOM_SID>27623</WAREN_NOM_SID>
 *	 <DAT_START>1971-12-31T00:00:00</DAT_START>
 *	 <DAT_END>4000-01-01T00:00:00</DAT_END>
 *	 <WAREN_NOM_ID>0100000000</WAREN_NOM_ID>
 *	 <WAREN_NOM_SUFFIX>80</WAREN_NOM_SUFFIX>
 *	 <LANG_BESCHR>LEBENDE TIERE</LANG_BESCHR>
 *	 <AEND_ART>i</AEND_ART>
 *	</Satz>
 *	<Satz>
 *	 <WAREN_NOM_BESCHR_SID>30964</WAREN_NOM_BESCHR_SID>
 *	 <WAREN_NOM_SID>27624</WAREN_NOM_SID>
 *	 <DAT_START>1972-01-01T00:00:00</DAT_START>
 *	 <DAT_END>4000-01-01T00:00:00</DAT_END>
 *	 <WAREN_NOM_ID>0101000000</WAREN_NOM_ID>
 *	 <WAREN_NOM_SUFFIX>80</WAREN_NOM_SUFFIX>
 *	 <LANG_BESCHR>Pferde, Esel, Maultiere und Maulesel, lebend</LANG_BESCHR>
 *	 <AEND_ART>i</AEND_ART>
 *	</Satz>
 * </T40010>
 * 
 * In this XML the "<Satz>" is the Line-Segment, and we assume that we want to have WAREN_NOM_SID and LANG_BESCHR in the output
 * so we use XMLCSVer that way:
 * 		XMLCSVer xmlcsver = new XMLCSVer();
 *		xmlcsver.setXMLFile("g:/services/IT/Gruppen/eBusiness/TeamEAI/XX_Schnittstellendoku/Grunddatenbestand/XD00997901_T40010_63.xml");
 *		xmlcsver.setCSVFile("g:/services/IT/Gruppen/eBusiness/TeamEAI/XX_Schnittstellendoku/Grunddatenbestand/XD00997901_T40010_63.csv");
 *		xmlcsver.setDelimiter(';');
 *		xmlcsver.setLineElement("Satz");
 *		xmlcsver.addOutputCSVField("WAREN_NOM_ID");
 *		xmlcsver.addOutputCSVField("LANG_BESCHR");
 *		xmlcsver.startParse();
 *
 *
 * }
 * 
 * @author pire
 * @version 1.0 (2016-07-27)
 *
 */
public class XMLCSVer extends DefaultHandler {

	private String xmlFileName;
	private String lineElement;
	private ArrayList<String> outputFieldList=new ArrayList<String>();
	private String[]outputLine;
	private int outputLinePointer;
	private int outputLineSize;
	private String csvFile;
	private char csvDelimiter;
	private CSVPrinter csvPrinter;
	
	/**
	 * called during parsing, not for external use! 
	 */
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		if (qName.equals(lineElement)) {
			// we found a new line, lets create a new line-buffer
			outputLine=new String[outputLineSize];
		}
		
		// see if we found one of the requested outputFields, if so, store index in outputLinePointer
		outputLinePointer=-1;
		for (int i=0;i<outputLineSize;i++) {
			if (qName.equals(outputFieldList.get(i))) outputLinePointer=i; 
		}
	}
	
	/**
	 * called during parsing, not for external use
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(lineElement)) {
			// we found the end of a line, append content to csv 
			try {
				csvPrinter.printRecord(outputLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		outputLinePointer=-1;	// end of any element means: we have not found anything useable!
	}

	/**
	 * called during parsing, not for external use
	 */
	public void characters(char ch[], int start, int length) throws SAXException {
		if (outputLinePointer==-1) return;	// we do not have found anything useable, just exit
		
		// ok, some useable data was found, just append them to the output-buffer
		if (outputLine[outputLinePointer]==null) outputLine[outputLinePointer]="";
		outputLine[outputLinePointer]+=new String (ch,start,length);
	}
	
	
	/**
	 * Starts the parsing process
	 * @return true on success, otherwise false
	 */
	public boolean startParse() {
		outputLineSize=outputFieldList.size();
		try {
			// prints header
			String[] headerLine=new String[outputLineSize];
			headerLine=outputFieldList.toArray(headerLine);
			csvPrinter=CSVFormat.DEFAULT.withDelimiter(csvDelimiter).withHeader(headerLine).print(new FileWriter(csvFile));
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		// starts the SAXParser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(xmlFileName,this);
			csvPrinter.close();
			return true;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * Setter XML Filename
	 * @param string
	 */
	public void setXMLFile(String string) {
		xmlFileName = string;		
	}


	/**
	 * Setter name of the line element
	 * @param string
	 */
	public void setLineElement(String string) {
		lineElement=string;
	}

	/**
	 * setter: add one output-field
	 * @param string
	 */
	public void addOutputCSVField(String string) {
		outputFieldList.add(string);
	}

	/**
	 * setter: sets the destination csv filename
	 * @param string
	 */
	public void setCSVFile(String string) {
		csvFile=string;
	}

	/**
	 * setter: sets the delimiter in csv file
	 * @param string
	 */
	public void setDelimiter(char string) {
		csvDelimiter=string;
	}
	

}


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.opencsv.CSVWriter;

public class XMLCSVer extends DefaultHandler {

	private String xmlFileName;
	private String lineElement;
	private ArrayList<String> outputFieldList=new ArrayList<String>();
	private String[]outputLine;
	private int outputLinePointer;
	private int outputLineSize;
	private String csvFile;
	private char csvDelimiter;
	private CSVWriter csvWriter;
	
	public void setXMLFile(String string) {
		xmlFileName = string;		
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		if (qName.equals(lineElement)) {
			outputLine=new String[outputLineSize];
		}
		outputLinePointer=-1;
		for (int i=0;i<outputLineSize;i++) {
			if (qName.equals(outputFieldList.get(i))) outputLinePointer=i; 
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(lineElement)) {
			csvWriter.writeNext(outputLine);
		}
		outputLinePointer=-1;
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if (outputLinePointer==-1) return;
		outputLine[outputLinePointer]=new String (ch,start,length);
	}
	
	
	public boolean startParse() {
		outputLineSize=outputFieldList.size();
		try {
			csvWriter=new CSVWriter(new FileWriter(csvFile), csvDelimiter);
			String[] headerLine=new String[outputLineSize];
			headerLine=outputFieldList.toArray(headerLine);
			csvWriter.writeNext(headerLine);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(xmlFileName,this);
			csvWriter.close();
			return true;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public void setLineElement(String string) {
		lineElement=string;
	}

	public void addOutputCSVField(String string) {
		outputFieldList.add(string);
	}

	public void setCSVFile(String string) {
		csvFile=string;
	}

	public void setDelimiter(char string) {
		csvDelimiter=string;
	}
	

}

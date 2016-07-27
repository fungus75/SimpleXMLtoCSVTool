
public class Main {

	public static void main(String[] args) {
		XMLCSVer xmlcsver = new XMLCSVer();
		xmlcsver.setXMLFile("g:/services/IT/Gruppen/eBusiness/TeamEAI/XX_Schnittstellendoku/Grunddatenbestand/XD00997901_T40010_63.xml");
		xmlcsver.setCSVFile("g:/services/IT/Gruppen/eBusiness/TeamEAI/XX_Schnittstellendoku/Grunddatenbestand/XD00997901_T40010_63.csv");
		xmlcsver.setDelimiter(';');
		xmlcsver.setLineElement("Satz");
		xmlcsver.addOutputCSVField("WAREN_NOM_ID");
		xmlcsver.addOutputCSVField("WAREN_NOM_SUFFIX");
		xmlcsver.addOutputCSVField("LANG_BESCHR");
		xmlcsver.startParse();
		

	}

}

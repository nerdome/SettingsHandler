import javanet.staxutils.IndentingXMLEventWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by fightcookie on 7/21/14.
 */
public class SettingsHandler {
	private static char[] alphabet;
	private static char[] vocals;
	//TODO need to use File instead of Path later?
	private static File configFile = Paths.get("c:/config.txt").toFile();

	public static void setSetting(String setting, char [] value) {
		switch (setting) {
			case "alphabet":
				alphabet = value;
				break;
			case "vocals":
				vocals = value;
				break;
			default:
				break;
		}
	}

	private static void addNode(XMLEventWriter writer, XMLEventFactory eventFactory, String setting, String value) throws XMLStreamException {
		try {
			writer.add(eventFactory.createStartElement("", "", setting));
			writer.add(eventFactory.createAttribute("value", value));
			writer.add(eventFactory.createEndElement("", "", setting));

		} catch (XMLStreamException e) {
			throw e;
		}
	}

	public static void writeToXml() throws XMLStreamException, IOException {
		XMLEventWriter eventWriter = null;
		XMLEventWriter writer = null;
		try (PrintWriter printWriter = new PrintWriter(configFile)) {

			eventWriter = XMLOutputFactory.newInstance().createXMLEventWriter(printWriter);
			writer = new IndentingXMLEventWriter(eventWriter);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			writer.add(eventFactory.createStartDocument());
			writer.add(eventFactory.createStartElement("", "", "config"));
			addNode(writer, eventFactory, "alphabet", new String(alphabet));
			addNode(writer, eventFactory, "vocals", new String(vocals));
			writer.add(eventFactory.createEndElement("", "", "config"));
			writer.add(eventFactory.createEndDocument());
			writer.flush();

		} catch (XMLStreamException | IOException e) {
			throw e;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
				if (eventWriter != null) {
					eventWriter.close();
				}
			} catch (XMLStreamException e) {
				throw e;
			}
		}
	}


	public static void readFromXml() {
		XMLEventReader reader = null;
		//TODO what file reader / writer to use? buffered?
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile))) {
			reader = XMLInputFactory.newInstance().createXMLEventReader(bufferedReader);
			while ((reader != null) && reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = (StartElement) event;
					QName value = QName.valueOf("value");
					switch (startElement.getName().toString()) {
						case "alphabet":
							alphabet = startElement.getAttributeByName(value).getValue().toCharArray();
							break;
						case "vocals":
							vocals = startElement.getAttributeByName(value).getValue().toCharArray();
							break;
						default:
							break;
					}
				}
			}
		} catch (XMLStreamException | IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (XMLStreamException e) {
					throw e;
				}
			}
		}
	}

	public static Path getConfigFile() {
		return configFile.toPath();
	}

	public static void setConfigFile(Path newConfigFile) throws IOException {
		configFile = newConfigFile.toRealPath().toFile();
	}
}
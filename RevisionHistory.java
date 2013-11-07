import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;



class RevisionHistory{
	String current;
	ArrayList<Revision> revisions;
	
	

	String printXML(){
		return null;
	}
	
	void readXML(String file) throws FileNotFoundException, XMLStreamException{
		InputStream is = new FileInputStream(file);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(is);
		String characters = null;
		Revision revision = null;
		
		while(eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();
			
			if(event.isStartElement()){
				StartElement startElement = event.asStartElement();
				String name = startElement.getName().getLocalPart();
				if(name.equals("revision")){
					revision = new Revision();
					Iterator<Attribute> itr = startElement.getAttributes();
					while(itr.hasNext()){
						Attribute attribute = itr.next();
						if(attribute.getName().equals("id"))
							revision.id = attribute.getValue();
						else if(attribute.getName().equals("location"))
							revision.location = attribute.getValue();
						else if(attribute.getName().equals("author"))
							revision.author = attribute.getValue();
						else if(attribute.getName().equals("timestamp"))
							revision.timestamp = attribute.getValue();
					}
				}
				else if(name.equals("revision-history")){
					Iterator<Attribute> itr = startElement.getAttributes();
					while(itr.hasNext()){
						Attribute attribute = itr.next();
						if(attribute.getName().equals("current"))
							current = attribute.getValue();
					}
				}
				else if(name.equals("parent")){
					Iterator<Attribute> itr = startElement.getAttributes();
					while(itr.hasNext()){
						Attribute attribute = itr.next();
						if(attribute.getName().equals("id"))
							revision.parents.add(attribute.getValue());
					}
				}
			}
			else if(event.isEndElement()){
				EndElement element = event.asEndElement();
				String name = element.getName().getLocalPart();
				if(name.equals("revision"))
					add(revision);
				else{
					switch(name){			
						case "comments": revision.comments = characters;
					}
				}
			}
			else if(event.isCharacters())
				characters = event.asCharacters().getData();
		}
	}
	
	void writeXML(String file) throws FileNotFoundException, XMLStreamException{
		FileOutputStream fos = new FileOutputStream(file);
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(fos);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent endln = eventFactory.createDTD("\n");
		
		eventWriter.add(eventFactory.createStartDocument());
		eventWriter.add(endln);
		
		eventWriter.add(eventFactory.createStartElement("", "", "revision-history")); //add attributes
		eventWriter.add(eventFactory.createAttribute("current", current));
		eventWriter.add(eventFactory.createAttribute("xmlns", "http://www.cs.uwm.edu/molhado/revision-history"));
		eventWriter.add(endln);
		
		for(Revision r:revisions){
			eventWriter.add(eventFactory.createStartElement("", "", "revision")); //add attributes
			eventWriter.add(eventFactory.createAttribute("id", r.id));
			eventWriter.add(eventFactory.createAttribute("author", r.author));
			eventWriter.add(eventFactory.createAttribute("timestamp", r.timestamp));
			eventWriter.add(eventFactory.createAttribute("location", r.location));
			eventWriter.add(endln);
			
			eventWriter.add(eventFactory.createStartElement("", "", "parent")); //add attributes
			for(String p:r.parents)
				eventWriter.add(eventFactory.createAttribute("id", p));
			eventWriter.add(eventFactory.createEndElement("", "", "parent"));
			eventWriter.add(endln);
			
			eventWriter.add(eventFactory.createStartElement("", "", "comments"));
			eventWriter.add(eventFactory.createCharacters(r.comments));
			eventWriter.add(eventFactory.createEndElement("", "", "comments"));
			eventWriter.add(endln);
		}
		
		eventWriter.add(eventFactory.createEndElement("", "", "revision-history"));
		eventWriter.add(endln);
		
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
	}
	
	Revision getRevision(String id){
		for(Revision r:revisions){
			if(r.id.equals(id))
				return r;
		}
		return null;
	}
	
	void add(Revision revision){
		revisions.add(revision);
	}
}

class Revision{
	String id, location, comments, author, timestamp;
	ArrayList<String> parents;
	
	Revision(){
		
	}
	
	Revision(String id){
		this.id = id;
	}
	
}
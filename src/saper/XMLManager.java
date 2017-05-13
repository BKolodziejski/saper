package saper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLManager {
	public static void saveDifficulty(DifficultyLevel diff) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(DifficultyLevel.class);
			Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            File test = new File(".", "difficulty.xml");
			test.createNewFile();
            m.marshal(diff, test);
		} catch (JAXBException | IOException e3) {
		}
	}
	
	public static DifficultyLevel loadDifficulty() {
		try {
    		JAXBContext context = JAXBContext.newInstance(DifficultyLevel.class);
        	Unmarshaller um = context.createUnmarshaller();
			return (DifficultyLevel) um.unmarshal(new FileReader("./difficulty.xml"));
    	}
		catch (JAXBException | FileNotFoundException e) {
			return DifficultyLevel.EASY;
		}
	}
}

package extractor.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

//给元素加上ID
public class AppendID {
	public static void AppendID(String filepath, String elementpath, String idString) throws Exception{
		SAXReader reader = new SAXReader();
		Document document = reader.read(filepath);
		Element e = (Element) document.selectSingleNode(elementpath);
		//System.out.println(e.attributeValue("name")+filepath);
		e.addAttribute("id", idString.toString());
		OutputStream outStream = new FileOutputStream(filepath);
		XMLWriter w = new XMLWriter(outStream);
		w.write(document);
		w.close();
	}
}

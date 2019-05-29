package extractor.service;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

//给元素加上ID
public class AppendID {
	public static void AppendID(String filepath, String elementpath, String idString) throws Exception{
		SAXReader reader = new SAXReader();
		Document document = reader.read(filepath);
		Element e = (Element) document.selectSingleNode(elementpath);
		try {			
			e.addAttribute("id", idString.toString());
		}catch(Exception e2) {
			//System.out.println(elementpath);
		}
		OutputStream outStream = new FileOutputStream(filepath);
		XMLWriter w = new XMLWriter(outStream);
		w.write(document);
		w.close();
	}
	//在.uml里面，xmi:id与id等价
	public static void AppendID4sysml(String filepath, String elementpath, String idString) throws Exception{
		SAXReader reader = new SAXReader();
		Document document = reader.read(filepath);
		Element e = (Element) document.selectSingleNode(elementpath);
		e.addAttribute("id4sysml", idString.toString());
		OutputStream outStream = new FileOutputStream(filepath);
		XMLWriter w = new XMLWriter(outStream);
		w.write(document);
		w.close();
	}
}

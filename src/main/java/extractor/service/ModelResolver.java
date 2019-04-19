package extractor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import extractor.model.Ports;
import extractor.model.component;
import extractor.model.connections;

//解析模型,本模块只负责模型文件的解析
@Service("ModelResolver")
public class ModelResolver {
	static List<? extends Node> components = null;
	static Map<String, component> componentlist = new HashMap<String, component>();
	static Map<String, Ports> portlist = new HashMap<String, Ports>();
	static Map<String, connections> conneclist = new HashMap<String, connections>();
	
	public static Document ModelResolver(String url) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		return document;
	}

	public static void ConnectionResolver(String modelfilename, String modelType) throws Exception {
		Document document = ModelResolver(modelfilename);
		Node sourcenode = null;
		Node destNode = null;
		List<? extends Node> connectionlist = new ArrayList<>();
		if (modelType.equals("aadl")) {
			// 读取connection
			connectionlist = document
					.selectNodes("//ownedClassifier[@xsi:type='aadl2:SystemImplementation']/ownedPropertyAssociation");
			connectionlist.forEach((v) -> {
				connections connections = new connections();
				Element element = (Element) v;
				String startpath = element.element("appliesTo").element("path").attributeValue("namedElement");
				element = (Element) document.selectSingleNode(GetXPath(startpath));
				component c = componentlist.get(element.attributeValue("name"));
				connections.setStartcomponentid(c.getComponentid());
				
				element = (Element) v;
				String endpath = element.element("ownedValue").element("ownedValue").element("ownedListElement")
						.element("path").attributeValue("namedElement");
				element = (Element) document.selectSingleNode(GetXPath(endpath));
				component c2 = componentlist.get(element.attributeValue("name"));

				connections.setEndcomponentid(c2.getComponentid());
				conneclist.put("", connections);
			});
//			sourcenode = document.selectSingleNode(
//					"//ownedClassifier[@xsi:type='aadl2:SystemImplementation']/ownedPropertyAssociation/appliesTo/path/@namedElement");
//			destNode = document.selectSingleNode(
//					"//ownedClassifier[@xsi:type='aadl2:SystemImplementation']/ownedPropertyAssociation/ownedValue/ownedValue/ownedListElement/path/@namedElement");

		}
	}

	// 当前模型的全部组件
	/* 与port */
	public static void MatchComponents(String modelfilename, String modelType) throws Exception {

		Document document = ModelResolver(modelfilename);

		List<String> namelist = new ArrayList<>();
//		List<String> descriptionlist = new ArrayList<>();
//		List<String> periodlist = new ArrayList<>();

//		Map<String, List<String>> connectionlist = new HashMap<String, List<String>>();
//		Map<String, List<String>> statelist = new HashMap<String, List<String>>();
//		Map<String, List<String>> Exceptionlist = new HashMap<String, List<String>>();

		if (modelType.equals("aadl")) {

			components = document.selectNodes(
					"//ownedClassifier[@xsi:type='aadl2:ThreadType' or @xsi:type='aadl2:ProcessType' or @xsi:type='aadl2:ProcessorType' or @xsi:type='aadl2:MemoryType']");

			List<? extends Node> ports = null;
			for (Node n : components) {
				Element element = (Element) n;
				component component = new component();

				Integer idString = (int) GetID.getId();
				component.setName("");
				component.setModeltype("aadl");
				component.setName(element.attributeValue("name"));
				component.setComponentid(idString);
				componentlist.put(element.attributeValue("name"), component);

				ports = element.elements("ownedDataPort");
				for (Node n2 : ports) {
					Element element2 = (Element) n2;
					Ports ports1 = new Ports();
					ports1.setPortname(element2.attributeValue("name"));
					ports1.setComponentid(idString);
					portlist.put(element2.attributeValue("name"), ports1);
				}
			}
		} else if (modelType.equals("sysml")) {
			components = document
					.selectNodes("//packagedElement[@name='kf']/ownedOperation[@xmi:type='uml:Operation']");
			for (Node n : components) {
				Element element = (Element) n;
				namelist.add(element.attributeValue("name"));
			}
		} else {
			components = document.selectNodes("//ModelInformation/Model/System/System/Block");
			for (Node n : components) {
				Element element = (Element) n;
				namelist.add(element.attributeValue("name"));
			}
		}
	}

	/*
	 * 根据路径获取ID
	 */
	private static String GetXPath(String path) {
		String reg = "(?<=@).[A-Za-z0-9\\.]+";
		ArrayList<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			result.add(matcher.group());
		}
		for (int j = 1; j < result.size(); j++) {
			
			result.set(j,getinc(result.get(j)));
		}
		return "//" + result.get(0) + "/" + result.get(1) + "]/" + result.get(2)+"]";
	}
	private static String getinc(String source) {
		String csub=source.substring(0,source.length()-2);
		String intsub=source.substring(source.length()-1);
		intsub=String.valueOf(Integer.parseInt(intsub)+1);
		return csub+"["+intsub;
		
	}
}

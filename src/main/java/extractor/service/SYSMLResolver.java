package extractor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import extractor.DAO.mapper.InvocationChannelMapper;
import extractor.DAO.mapper.MessageChannelMapper;
import extractor.DAO.mapper._eventMapper;
import extractor.DAO.mapper._exceptionMapper;
import extractor.DAO.mapper._provideMapper;
import extractor.DAO.mapper._requireMapper;
import extractor.DAO.mapper._stateMapper;
import extractor.DAO.mapper._taskMapper;
import extractor.DAO.mapper.busMapper;
import extractor.DAO.mapper.communicationchannelMapper;
import extractor.DAO.mapper.componentMapper;
import extractor.DAO.mapper.dataobjectMapper;
import extractor.DAO.mapper.deviceMapper;
import extractor.DAO.mapper.linkpointMapper;
import extractor.DAO.mapper.rtosMapper;
import extractor.DAO.mapper.transitionMapper;
import extractor.DAO.mapper.transitionstateMapper;
import extractor.model.InvocationChannel;
import extractor.model.MessageChannel;
import extractor.model._event;
import extractor.model._exception;
import extractor.model._provide;
import extractor.model._require;
import extractor.model._state;
import extractor.model._task;
import extractor.model.bus;
import extractor.model.communicationchannel;
import extractor.model.component;
import extractor.model.dataobject;
import extractor.model.device;
import extractor.model.linkpoint;
import extractor.model.rtos;
import extractor.model.transition;
import extractor.model.transitionstate;
import extractor.util.AppendID;
import extractor.util.GetID;

@Service("SYSMLResolver")
public class SYSMLResolver {
	Map<String, String> sysmlFiles = new HashMap<String, String>();
	static String modeldirectory;
	static List<component> componentlist = new ArrayList<component>();
	static List<linkpoint> portlist = new ArrayList<linkpoint>();
	static List<_require> requirelist = new ArrayList<_require>();
	static List<_provide> providelist = new ArrayList<_provide>();

	static List<_exception> exceptionlist = new ArrayList<_exception>();
	static List<communicationchannel> cclist = new ArrayList<communicationchannel>();
	static List<_task> tasklist = new ArrayList<_task>();
	@Autowired
	private componentMapper camArchMapper;
	@Autowired
	private linkpointMapper portsMapper;
	@Autowired
	private _provideMapper pM;
	@Autowired
	private _requireMapper rm;
	@Autowired
	private transitionMapper tm;
	@Autowired
	private transitionstateMapper tsm;
	@Autowired
	private _eventMapper em;
	@Autowired
	private _stateMapper sm;
	@Autowired
	private deviceMapper dMapper;
	@Autowired
	private busMapper bm;
	@Autowired
	private communicationchannelMapper cchannelMapper;
	@Autowired
	private MessageChannelMapper mcm;
	@Autowired
	private InvocationChannelMapper ivcm;
	@Autowired
	private _exceptionMapper _em;
	@Autowired
	private rtosMapper rsmMapper;
	@Autowired
	private _taskMapper _tm;
	@Autowired
	private dataobjectMapper dobjm;

	private int insert_component(component c) {
		return camArchMapper.insert(c);
	}

	private int insert_bus(bus b) {
		return bm.insert(b);
	}

	private int insert_ports(linkpoint p) {
		return portsMapper.insert(p);
	}

	private int insert_require(_require r) {
		return rm.insert(r);
	}

	private int insert_provide(_provide p) {
		return pM.insert(p);
	}

	private int insert_cchannel(communicationchannel c) {
		return cchannelMapper.insert(c);
	}

	private int insert_mchannel(MessageChannel m) {
		return mcm.insert(m);
	}

	private int insert_ivcchannel(InvocationChannel i) {
		return ivcm.insert(i);
	}

	private int insert_device(device d) {
		return dMapper.insert(d);
	}

	private int insert_task(_task t) {
		return _tm.insert(t);
	}

	private int insert_transition(transition t) {
		return tm.insert(t);
	}

	private int insert_tss(transitionstate ts) {
		return tsm.insert(ts);
	}

	private int insert_rtos(rtos r) {
		return rsmMapper.insert(r);
	}

	private int insert_state(_state s) {
		return sm.insert(s);
	}

	private int insert_event(_event e) {
		return em.insert(e);
	}

	private int insert_exception(_exception e) {
		return _em.insert(e);
	}

	public Integer getPortIDByComponentName(String name, String portname) {
		Integer aIntegers = camArchMapper.getPortIDByComponentName(name, portname);
		return camArchMapper.getPortIDByComponentName(name, portname);
	}

	public Integer getCmpIDbyName(String name) {
		return camArchMapper.getIDbyName(name).getComponentid();
	}

	public Integer getCChannelBysd(Integer sid, Integer did) {
		return cchannelMapper.getCChannelBysd(sid, did);
	}

	public Integer getEventID(String eventname) {
		return em.getEventID(eventname).getEventid();
	}

	public Integer getStateID(String statename) {
		return sm.getStateID(statename);
	}

	public void setSysmlFiles(Map<String, String> sysmlFiles) {
		this.sysmlFiles = sysmlFiles;
	}

	public void srvmatchmeta() throws Exception {
		modeldirectory = "src/main/resources/modelresource/MarkedModelFile/";
		MatchComponents(sysmlFiles.get("文件"), "块图");

		tasklist.forEach((v) -> {
			insert_task(v);
		});
		exceptionlist.forEach((v) -> {
			insert_exception(v);
		});
		MatchCChannel(sysmlFiles.get("文件"), "块图");

	}

	public static Document ModelResolver(String url) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		return document;
	}

	public void MatchComponents(String filepath, String contenttype) throws Exception {
		Document document = ModelResolver(filepath);

		String getCompponents = "//packagedElement[@xmi:type='uml:Class']/nestedClassifier[@xmi:type='uml:Class' or @xmi:type='uml:Device']";
		List<? extends Node> com = document.selectNodes(getCompponents);
		for (Node n : com) {
			Element e = (Element) n;
			component c = new component();
			// 自带的xmi:id
			Integer idString = (int) GetID.getId();
			AppendID.AppendID4sysml(filepath, n.getUniquePath(), idString.toString());
			c.setComponentid(idString);
			c.setName(e.attributeValue("name"));
			c.setModeltype("sysml");
			// sysml组件种类鉴别
			Element root = document.getRootElement();
			QName q = QName.get("type", root.getNamespaceForPrefix("xmi"));
			if (e.attributeValue(q).equals("uml:Class")) {
				c.setType("rtos");
			} else {
				c.setType("device");
			}
			Element wcetElement = (Element) document
					.selectSingleNode(e.getUniquePath() + "/ownedAttribute[@name='delay']");
			if (wcetElement != null) {
				c.setWcet(wcetElement.element("defaultValue").attributeValue("value") + "ms");
			}
			insert_component(c);
//			if (e.element("ownedRule[@xmi:type='uml:Constraint']") != null) {
//				Element e2 = e.element("ownedRule[@xmi:type='uml:Constraint']");
//				_exception ex = new _exception();
//				ex.setName(e2.attributeValue("name"));
//				exceptionlist.add(ex);
//			}
			List<Element> opElements = e.elements("ownedOperation");
			opElements.forEach((vop) -> {
				if (vop.attribute("raisedException") != null) {

					String exid = vop.attributeValue("raisedException");
					// 多个异常
					if (exid.contains(" ")) {
						String[] exids = exid.split(" ");
						for (String s : exids) {
							_exception ex = new _exception();
							Element excElement = (Element) document
									.selectSingleNode("//nestedClassifier[@xmi:id='" + s + "']");
							ex.setName(excElement.attributeValue("name"));
							ex.setType("sysmlexception");
							ex.setComponentid(Integer.valueOf(idString));
							exceptionlist.add(ex);
						}
					} else {

						_exception ex = new _exception();
						Element excElement = (Element) document
								.selectSingleNode("//nestedClassifier[@xmi:id='" + exid + "']");
						ex.setName(excElement.attributeValue("name"));
						ex.setType("sysmlexception");
						ex.setComponentid(Integer.valueOf(idString));
						exceptionlist.add(ex);
					}
				}
			});
			// component的operation是错误处理的
//			if (e.element("ownedOperation") != null) {
//				String e2 = e.element("ownedOperation").attributeValue("name");
//				String exceptionlistid = ((Element) document.selectSingleNode("//packagedElement[@name='" + e2 + "']"))
//						.attributeValue("id");
//				String[] exceptionlist = ((Element) document
//						.selectSingleNode("//Requirements:Requirement[@base_Class='" + exceptionlistid + "']"))
//								.attributeValue("text").split("、");
//				for (String s : exceptionlist) {
//					_exception ex = new _exception();
//					ex.setType("sysmlexception");
//
//					ex.setName(s);
//					ex.setComponentid(Integer.valueOf(idString));
//					insert_exception(ex);
//				}
//			}
			LinkpointResolver(filepath, e.getUniquePath(), idString, "subsys");
			TaskResolver(filepath, n.getUniquePath(), c);
		}
	}

	private void LinkpointResolver(String linkpointfile, String fatherpath, Integer fatherid, String fathertype)
			throws Exception {
		Document document = ModelResolver(linkpointfile);

		List<? extends Node> ports = document.selectNodes(fatherpath + "/ownedAttribute[@xmi:type='uml:Port']");
		for (Node n : ports) {
			Element element2 = (Element) n;
			linkpoint ports1 = new linkpoint();
			ports1.setName(element2.attributeValue("name"));
			ports1.setModeltype("sysml");
			Integer linkpointID = (int) GetID.getId();
			ports1.setLinkpointid(linkpointID);

//			try {
			AppendID.AppendID4sysml(linkpointfile, element2.getUniquePath(), linkpointID.toString());
			// type指向赋予id
			QName qname1 = QName.get("type");

			if (element2.attributeValue(qname1) != null) {

				String g = "//nestedClassifier[@xmi:id='" + element2.attributeValue(qname1) + "']";
				AppendID.AppendID4sysml(linkpointfile, g, linkpointID.toString());
				Element dataElement = (Element) document.selectSingleNode(g);

				Element periodelElement = (Element) document
						.selectSingleNode(dataElement.getUniquePath() + "/ownedAttribute[@name='period']/defaultValue");
				if (periodelElement != null) {

					ports1.setPeriod(periodelElement.attributeValue("value") + "ms");
				}
				Element protocolElement = (Element) document.selectSingleNode(
						dataElement.getUniquePath() + "/ownedAttribute[@name='communicationProtocol']/defaultValue");
				if (protocolElement != null) {

					ports1.setProtocol(protocolElement.attributeValue("value"));
				}
				
				dataobject dobj = new dataobject();
				dobj.setDatatype(dataElement.attributeValue("name"));
				dobj.setFrom(linkpointID);
				dobjm.insert(dobj);
			}
			insert_ports(ports1);
			if (element2.element("ownedComment") != null) {
				Element direcElement = element2.element("ownedComment").element("body");
				if (direcElement.getText().contains("in")) {
					_require r = new _require();
					r.setRequired(Integer.valueOf(linkpointID));
					r.setRequirer(fatherid);
					insert_require(r);
				} else {
					_provide p = new _provide();
					p.setProvided(Integer.valueOf(linkpointID));
					p.setProvider(fatherid);
					insert_provide(p);
				}
			} else {
				// 双向端口
				_require r = new _require();
				r.setRequired(Integer.valueOf(linkpointID));
				r.setRequirer(fatherid);
				insert_require(r);

				_provide p = new _provide();
				p.setProvided(Integer.valueOf(linkpointID));
				p.setProvider(fatherid);
				insert_provide(p);
			}
		}
	}

	public void MatchCChannel(String modelfilename, String modelType) throws Exception {
		Document document = ModelResolver(modelfilename);
		String getmessagechannel = "//packagedElement[@xmi:type='uml:InformationFlow']";
		// String getmessagechannel = "//packagedElement[@xmi:type='uml:Association']";
		List<? extends Node> namelist = document.selectNodes(getmessagechannel);
		for (Node n : namelist) {
			Element element = (Element) n;
			communicationchannel cchannel = new communicationchannel();
			Integer idString = (int) GetID.getId();
			AppendID.AppendID4sysml(modelfilename, n.getUniquePath(), idString.toString());
			// sysml没有分类一律按照sync处理
			if (element.attributeValue("informationSource") != null) {
				cchannel.setModeltype("sysml");
				cchannel.setType("sync");
				cchannel.setName(element.attributeValue("name"));
				cchannel.setCommunicationchannelid(idString);
				Integer portid = GetCMPIDByXMIID(modelfilename, element.attributeValue("informationSource"));
				cchannel.setSourceid(portid);

				// Updateportinfo(modelfilename, element.attributeValue("informationSource"),
				// "out");
			}

			if (element.attributeValue("informationTarget") != null) {

				cchannel.setDestid(GetCMPIDByXMIID(modelfilename, element.attributeValue("informationTarget")));
				// Updateportinfo(modelfilename, element.attributeValue("informationTarget"),
				// "in");
				insert_cchannel(cchannel);

			}

		}
	}

	private void TaskResolver(String modelfilename, String fatherpath, component father) throws Exception {
		Document document = ModelResolver(modelfilename);
//		String gettask = fatherpath + "/ownedOperation[@xmi:type='uml:Operation']";
		String gettask = fatherpath + "/nestedClassifier";
		List<? extends Node> namelist = document.selectNodes(gettask);
		for (Node n : namelist) {
			Element taskElement = (Element) n;
			component taskcomponent = new component();
			Integer idString = (int) GetID.getId();
			taskcomponent.setComponentid(idString);

			taskcomponent.setModeltype("sysml");
			taskcomponent.setName(taskElement.attributeValue("name"));
			taskcomponent.setType("task");
			insert_component(taskcomponent);
			_task t = new _task();
			t.setName(taskElement.attributeValue("name"));
			t.setTaskid(idString);
			t.setFatherid(father.getComponentid());
			Element wcetElement = (Element) document
					.selectSingleNode(taskElement.getUniquePath() + "/ownedAttribute[@name='delay']");
			if (wcetElement != null) {

				t.setWcet(wcetElement.element("defaultValue").attributeValue("value") + "ms");
			}
			Element periodElement = (Element) document
					.selectSingleNode(taskElement.getUniquePath() + "/ownedAttribute[@name='period']");
			if (periodElement != null) {

				t.setPeriod(periodElement.element("defaultValue").attributeValue("value") + "ms");
			}
			// 块图的partition不存在
			try {
				AppendID.AppendID4sysml(modelfilename, taskElement.getUniquePath(), t.getTaskid().toString());
				LinkpointResolver(modelfilename, taskElement.getUniquePath(), idString, "task");
			} catch (Exception e) {
				e.printStackTrace();
			}
			insert_task(t);
			threadResolver(modelfilename, n.getUniquePath(), t);
			// operation即task有错误定义
			if (taskElement.element("ownedRule") != null) {
				// Element e2 = element2.element("ownedRule[@xmi:type='uml:Constraint']");
				Element e2 = taskElement.element("ownedRule");
				_exception ex = new _exception();
				ex.setName(e2.attributeValue("name"));
				exceptionlist.add(ex);
			}
		}

	}

	private void threadResolver(String modelfilename, String fatherpath, _task father) throws Exception {
		Document document = ModelResolver(modelfilename);
		List<Node> threadnodes = document.selectNodes(fatherpath + "/ownedOperation");
		for (Node n : threadnodes) {
			Element threadElement = (Element) n;
			component threadcomponent = new component();
			Integer idString = (int) GetID.getId();
			threadcomponent.setComponentid(idString);

			threadcomponent.setModeltype("sysml");
			threadcomponent.setName(threadElement.attributeValue("name"));
			threadcomponent.setType("task");
			insert_component(threadcomponent);

			AppendID.AppendID(modelfilename, n.getUniquePath(), idString.toString());

			_task task = new _task();
			task.setName(threadElement.attributeValue("name"));
			task.setTaskid(idString);
			task.setFatherid(father.getTaskid());
			Element wcetElement = (Element) document
					.selectSingleNode(threadElement.getUniquePath() + "/ownedParameter[@name='delay']");
			if (wcetElement != null) {

				task.setWcet(wcetElement.element("defaultValue").attributeValue("value") + "ms");
			}
			Element periodElement = (Element) document
					.selectSingleNode(threadElement.getUniquePath() + "/ownedParameter[@name='period']");
			if (periodElement != null) {

				task.setPeriod(periodElement.element("defaultValue").attributeValue("value") + "ms");
			}
			LinkpointResolver(modelfilename, threadElement.getUniquePath(), idString, "task");
			insert_task(task);
		}
	}

	private Integer GetCMPIDByXMIID(String filepath, String id) throws Exception {
		Document document = ModelResolver(filepath);
		String g = "//ownedAttribute[@xmi:id='" + id + "']";
		Element element = (Element) document.selectSingleNode(g);
		if (element != null) {

			return Integer.valueOf(element.attributeValue("id4sysml"));
		}
		return 0;
	}

	private void Updateportinfo(String modelfilename, String portid, String direction) throws Exception {
		Document document = ModelResolver(modelfilename);
		Element portElement = (Element) document.selectSingleNode("//ownedAttribute[@xmi:id='" + portid + "']");
		// 存在连到block的情况
		if (portElement != null) {

			Element parentcompElement = portElement.getParent();
			if (direction.equals("in")) {
				_require r = new _require();
				r.setRequired(Integer.valueOf(portElement.attributeValue("id4sysml")));
				r.setRequirer(Integer.valueOf(parentcompElement.attributeValue("id4sysml")));
				insert_require(r);
			} else {
				_provide p = new _provide();
				p.setProvided(Integer.valueOf(portElement.attributeValue("id4sysml")));
				p.setProvider(Integer.valueOf(parentcompElement.attributeValue("id4sysml")));
				insert_provide(p);
			}
		}
	}
}

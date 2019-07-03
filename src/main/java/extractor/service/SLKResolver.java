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
import extractor.DAO.mapper.componenttransitionMapper;
import extractor.DAO.mapper.dataobjectMapper;
import extractor.DAO.mapper.deviceMapper;
import extractor.DAO.mapper.linkpointMapper;
import extractor.DAO.mapper.rtosMapper;
import extractor.DAO.mapper.slkstateMapper;
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
import extractor.model.componenttransition;
import extractor.model.dataobject;
import extractor.model.device;
import extractor.model.linkpoint;
import extractor.model.rtos;
import extractor.model.slkstate;
import extractor.model.transition;
import extractor.model.transitionstate;

@Service("SLKResolver")

public class SLKResolver {
	Map<String, String> slkfile = new HashMap<String, String>();
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
	private componenttransitionMapper cttm;
	@Autowired
	private linkpointMapper portsMapper;
	@Autowired
	private dataobjectMapper dom;
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
	private slkstateMapper slksm;

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

	public void setSlkfile(Map<String, String> slkfile) {
		this.slkfile = slkfile;
	}

	public static Document ModelResolver(String url) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		return document;
	}

	public void srvmatchmeta() throws Exception {
		modeldirectory = "src/main/resources/modelresource/MarkedModelFile/";
		MatchComponents(slkfile.get("文件"));
		componentlist.forEach((v) -> {
			insert_component(v);
		});
		portlist.forEach((v) -> {
			insert_ports(v);
		});
		tasklist.forEach((v) -> {
			insert_task(v);
		});
		exceptionlist.forEach((v) -> {
			insert_exception(v);
		});
		MatchCChannel(slkfile.get("文件"), "块图");
//		cclist.forEach((v) -> {
//			insert_cchannel(v);
//		});
	}

	public void MatchComponents(String filepath) throws Exception {
		Document document = ModelResolver(filepath);

		String getCompponents = "//System/Block[@BlockType='SubSystem']";
		List<? extends Node> com = document.selectNodes(getCompponents);
		for (Node n : com) {
			Element e = (Element) n;
			component c = new component();

			Integer idString = (int) GetID.getId();
			AppendID.AppendID(filepath, n.getUniquePath(), idString.toString());
			c.setComponentid(idString);
			c.setName(e.attributeValue("Name"));
			c.setModeltype("simulink");
			// 并非所有的都有
			Element props = (Element) document.selectSingleNode(n.getUniquePath() + "/P[@Name='Description']");
			c.setType("rtos");
			if (props != null) {
				String[] ps = props.getText().split("\n");
				for (String s : ps) {
					if (s.contains("wcet")) {
						String[] wcet = s.split(" ");
						c.setWcet(wcet[2]);
					}
				}
			}
			insert_component(c);

			LinkpointResolver(filepath, n.getUniquePath(), c);
			StateResolver(filepath, c);
			transitionResolver(filepath, c);
		}
	}

	private void LinkpointResolver(String linkpointfile, String fatherpath, component father) throws Exception {
		Document document = ModelResolver(linkpointfile);
		List<? extends Node> outports = document.selectNodes(fatherpath + "/System/Block[@BlockType='Outport']");
		List<? extends Node> inports = document.selectNodes(fatherpath + "/System/Block[@BlockType='Inport']");
		for (Node n : outports) {
			Element element2 = (Element) n;
			linkpoint ports1 = new linkpoint();
			ports1.setName(element2.attributeValue("Name"));
			ports1.setModeltype("simulink");
			Integer linkpointID = (int) GetID.getId();
			ports1.setLinkpointid(linkpointID);
			try {
				AppendID.AppendID(linkpointfile, element2.getUniquePath(), linkpointID.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 端口的数据
			Element portdata = (Element) document
					.selectSingleNode("//chart/Children/data[@name='" + element2.attributeValue("Name") + "']");
			Element portdatadescription = (Element) document
					.selectSingleNode(portdata.getUniquePath() + "/P[@Name='description']");
			if (portdatadescription != null) {
				String[] att = portdatadescription.getText().split("=");
				ports1.setPeriod(att[1] + "ms");
			}

			Element typElement = (Element) document.selectSingleNode(portdata.getUniquePath() + "/P[@Name='dataType']");
			if (typElement != null) {
				dataobject d = new dataobject();
				d.setFrom(linkpointID);
//				d.setTo(to);
				d.setDatatype(typElement.getText());
				dom.insert(d);
			}

			insert_ports(ports1);
			_provide pvd = new _provide();
			pvd.setProvider(father.getComponentid());
			pvd.setProvided(linkpointID);
			insert_provide(pvd);

		}
		for (Node n : inports) {
			Element element2 = (Element) n;
			linkpoint ports1 = new linkpoint();
			ports1.setName(element2.attributeValue("Name"));
			ports1.setModeltype("simulink");
			Integer linkpointID = (int) GetID.getId();
			ports1.setLinkpointid(linkpointID);
			try {
				AppendID.AppendID(linkpointfile, element2.getUniquePath(), linkpointID.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Element portdata = (Element) document
					.selectSingleNode("//chart/Children/data[@name='" + element2.attributeValue("Name") + "']");
			Element portdatadescription = (Element) document
					.selectSingleNode(portdata.getUniquePath() + "/P[@Name='description']");

			if (portdatadescription != null) {
				String[] att = portdatadescription.getText().split("=");
				ports1.setPeriod(att[1] + "ms");
			}
			insert_ports(ports1);

			_require req = new _require();
			req.setRequirer(father.getComponentid());
			req.setRequired(linkpointID);
			insert_require(req);
		}
		// 寻找linkpoint的属性

	}

	private void StateResolver(String modelfilename, component father) throws Exception {
		Document document = ModelResolver(modelfilename);
		String gettask = "//Stateflow/machine/Children/chart/P[contains(text(),'" + father.getName()
				+ "')]/following-sibling::Children/state";
		List<? extends Node> namelist = document.selectNodes(gettask);
		for (Node n : namelist) {
			Element taskElement = (Element) n;
			component taskcomponent = new component();
			Integer idString = (int) GetID.getId();
			taskcomponent.setComponentid(idString);
			taskcomponent.setModeltype("simulink");
			Element ls = (Element) document.selectSingleNode(taskElement.getUniquePath() + "/P[@Name='labelString']");

			String[] ps = ls.getText().split("\n");

			taskcomponent.setName(ps[0]);
			slkstate sks = new slkstate();
			sks.setTaskid(idString.toString());
//从名字中解析exit
			String regString = "(?<=exit:).*";
			Pattern pattern = Pattern.compile(regString);
			Matcher m = pattern.matcher(ls.getText());
			if (m.find()) {

				sks.setExitinfo(m.group());
			}

			taskcomponent.setType("task");

			_task t = new _task();
			Element dElement = (Element) document
					.selectSingleNode(taskElement.getUniquePath() + "/P[@Name='description']");
			if (dElement != null) {
				String[] props = dElement.getText().split("\n");
				for (String s : props) {
					if (s.contains("faultType")) {
						_exception e = new _exception();
						e.setName(s.split("=")[1]);
						e.setComponentid(idString);
						_em.insert(e);
					}
					if (s.contains("faultState")) {
						sks.setSlkstatecol(s.split("=")[1]);
						slksm.insert(sks);
					}
				}
				t.setWcet(dElement.getText().split(" ")[2]);
				taskcomponent.setWcet(dElement.getText().split(" ")[2]);
			}
			insert_component(taskcomponent);
			t.setName(ps[0]);
			t.setTaskid(idString);
			t.setFatherid(father.getComponentid());
//			try {
			AppendID.AppendID(modelfilename, taskElement.getUniquePath(), t.getTaskid().toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			insert_task(t);

			// 有thread
			Element childElement = taskElement.element("Children");
			if (childElement != null) {
				Document doc = ModelResolver(modelfilename);
				List<? extends Node> threalist = doc.selectNodes(childElement.getUniquePath() + "/state");
				for (Node n2 : threalist) {
					Element threadElement = (Element) n2;
					component threadcomp = new component();
					Integer threadid = (int) GetID.getId();
					threadcomp.setComponentid(threadid);
					threadcomp.setModeltype("simulink");
					Element threadnamElement = (Element) doc
							.selectSingleNode(threadElement.getUniquePath() + "/P[@Name='labelString']");

					String[] threadprop = threadnamElement.getText().split("\n");

					threadcomp.setName(threadprop[0]);

					slkstate sks2 = new slkstate();
					sks2.setTaskid(threadid.toString());

					String regString2 = "(?<=exit:).*";
					Pattern pattern2 = Pattern.compile(regString2);
					Matcher m2 = pattern2.matcher(threadnamElement.getText());
					if (m2.find()) {
						sks2.setExitinfo(m2.group());
					}

					Element descElement = (Element) doc
							.selectSingleNode(threadElement.getUniquePath() + "/P[@Name='description']");
					_task threadTask = new _task();
					if (descElement != null) {
						String[] props = descElement.getText().split("\n");
						for (String s : props) {
							if (s.contains("faultType")) {
								_exception e2 = new _exception();
								e2.setName(s.split("=")[1]);
								e2.setComponentid(threadid);
								_em.insert(e2);
							}
							if (s.contains("faultState")) {
								sks2.setSlkstatecol(s.split("=")[1]);
								slksm.insert(sks2);
							}
							if (s.contains("wcet")) {
								threadcomp.setWcet(s.split(" ")[2]);
								threadTask.setWcet(s.split(" ")[2]);
							}
						}
					}

					threadcomp.setType("task");
					insert_component(threadcomp);

					threadTask.setName(threadprop[0]);
					threadTask.setTaskid(threadid);
					threadTask.setFatherid(idString);
//					try {
					AppendID.AppendID(modelfilename, threadElement.getUniquePath(), threadTask.getTaskid().toString());

					insert_task(threadTask);
				}
				transitionResolver4thread(modelfilename,taskcomponent , taskElement.getUniquePath());
			}
		}
	}

	private void transitionResolver4thread(String modelfilename, component father, String fatherpath) throws Exception {

		Document document = ModelResolver(modelfilename);
		String gettransition = fatherpath + "/Children/transition";
		List<Node> transitionlist = document.selectNodes(gettransition);
		for (Node n : transitionlist) {
			Element transitionElement = (Element) n;
			Integer idString = (int) GetID.getId();
			transition t = new transition();
			t.setTransitionid(idString);
			AppendID.AppendID(modelfilename, n.getUniquePath(), idString.toString());
			// trigger要从source找
			Element idElement = (Element) document.selectSingleNode(n.getUniquePath() + "/src/P[@Name='SSID']");
			if (idElement != null) {

				Map<String, String> r = getstateinfo(modelfilename, transitionElement, idElement.getText());
				_event e = new _event();
				Integer eventid = (int) GetID.getId();
				e.setEventid(eventid);
				e.setName(r.get("trigger"));
				insert_event(e);

				t.setTriggerid(eventid);
				Element attreElement = (Element) document
						.selectSingleNode(n.getUniquePath() + "/P[@Name='labelString']");
				t.setName(attreElement.getText());
				insert_transition(t);
				transitionstate ts = new transitionstate();
				ts.setSourceid(Integer.valueOf(r.get("id")));
				ts.setTransitionid(idString);
				Element idElement2 = (Element) document.selectSingleNode(n.getUniquePath() + "/dst/P[@Name='SSID']");
				Map<String, String> r2 = getstateinfo(modelfilename, transitionElement, idElement2.getText());
				ts.setOutid(Integer.valueOf(r2.get("id")));
				insert_tss(ts);
				componenttransition ctt = new componenttransition();
				ctt.setComponentid(father.getComponentid().toString());
				ctt.setTransitionid(idString.toString());
				cttm.insert(ctt);
			}
		}
	}

	private void transitionResolver(String modelfilename, component father) throws Exception {
		Document document = ModelResolver(modelfilename);
		String gettransition = "//Stateflow/machine/Children/chart/P[contains(text(),'" + father.getName()
				+ "')]/following-sibling::Children/transition";
		List<Node> transitionlist = document.selectNodes(gettransition);
		for (Node n : transitionlist) {
			Element transitionElement = (Element) n;
			Integer idString = (int) GetID.getId();
			transition t = new transition();
			t.setTransitionid(idString);
			AppendID.AppendID(modelfilename, n.getUniquePath(), idString.toString());
			// trigger要从source找
			Element idElement = (Element) document.selectSingleNode(n.getUniquePath() + "/src/P[@Name='SSID']");
			if (idElement != null) {

				Map<String, String> r = getstateinfo(modelfilename, transitionElement, idElement.getText());
				_event e = new _event();
				Integer eventid = (int) GetID.getId();
				e.setEventid(eventid);
				e.setName(r.get("trigger"));
				insert_event(e);

				t.setTriggerid(eventid);
				Element attreElement = (Element) document
						.selectSingleNode(n.getUniquePath() + "/P[@Name='labelString']");
				t.setName(attreElement.getText());
				insert_transition(t);
				transitionstate ts = new transitionstate();
				ts.setSourceid(Integer.valueOf(r.get("id")));
				ts.setTransitionid(idString);
				Element idElement2 = (Element) document.selectSingleNode(n.getUniquePath() + "/dst/P[@Name='SSID']");
				Map<String, String> r2 = getstateinfo(modelfilename, transitionElement, idElement2.getText());
				ts.setOutid(Integer.valueOf(r2.get("id")));
				insert_tss(ts);
				componenttransition ctt = new componenttransition();
				ctt.setComponentid(father.getComponentid().toString());
				ctt.setTransitionid(idString.toString());
				cttm.insert(ctt);
			}
		}
	}

	public void MatchCChannel(String modelfilename, String modelType) throws Exception {
		Document document = ModelResolver(modelfilename);

		String getmessagechannel = "//Model/System/Line";
		List<? extends Node> namelist = document.selectNodes(getmessagechannel);
		for (Node n : namelist) {
			Element element = (Element) n;

			communicationchannel cchannel = new communicationchannel();
			Integer idString = (int) GetID.getId();
			//
			cchannel.setModeltype("simulink");
			cchannel.setType("sync");
			cchannel.setName("linkinslk");
			cchannel.setCommunicationchannelid(idString);
			// 有branch情况
			if (element.element("Branch") != null) {
				List<Element> braches = element.elements("Branch");
				for (Element e : braches) {
					Integer id4branch = (int) GetID.getId();
					AppendID.AppendID(modelfilename, e.getUniquePath(), id4branch.toString());
					cchannel.setCommunicationchannelid(id4branch);

					Element sb = (Element) document.selectSingleNode(element.getUniquePath() + "/P[@Name='SrcBlock']");
					String blockname = sb.getText();
					// port的sourceid
					String port = document
							.selectSingleNode(sb.getUniquePath() + "/following-sibling::P[@Name='SrcPort']").getText();
					blockname = blockname.replace("\n", " ");
					Element sourcecmp = (Element) document
							.selectSingleNode("//Model/System/Block[@Name='" + blockname + "']");
					// 该组件的所有端口
					List<Node> portList = document.selectNodes(
							sourcecmp.getUniquePath() + "/System/Block[@BlockType='Inport' or @BlockType='Outport']");
					GetPortByID(document, cchannel, port, portList, "source");

					// dest
					Element db = (Element) document.selectSingleNode(e.getUniquePath() + "/P[@Name='DstBlock']");
					blockname = db.getText().replace("\n", " ");
					// dstportid
					port = document.selectSingleNode(db.getUniquePath() + "/following-sibling::P[@Name='DstPort']")
							.getText();

					Element dstcmp = (Element) document
							.selectSingleNode("//Model/System/Block[@Name='" + blockname + "']");
					portList = document.selectNodes(
							dstcmp.getUniquePath() + "/System/Block[@BlockType='Inport' or @BlockType='Outport']");
					GetPortByID(document, cchannel, port, portList, "dest");

					insert_cchannel(cchannel);
				}

			} else {
				AppendID.AppendID(modelfilename, n.getUniquePath(), idString.toString());
				Element sb = (Element) document.selectSingleNode(element.getUniquePath() + "/P[@Name='SrcBlock']");
				Element db = (Element) document.selectSingleNode(element.getUniquePath() + "/P[@Name='DstBlock']");
				String blockname = sb.getText();
				String port = document.selectSingleNode(sb.getUniquePath() + "/following-sibling::P[@Name='SrcPort']")
						.getText();
				// blockname=blockname.replace("\n", "&#xA;");
				blockname = blockname.replace("\n", " ");
				Element sourcecmp = (Element) document
						.selectSingleNode("//Model/System/Block[@Name='" + blockname + "']");

				// 该组件的所有端口
				List<Node> portList = document.selectNodes(
						sourcecmp.getUniquePath() + "/System/Block[@BlockType='Inport' or @BlockType='Outport']");
				GetPortByID(document, cchannel, port, portList, "source");

				blockname = db.getText().replace("\n", " ");
				port = document.selectSingleNode(db.getUniquePath() + "/following-sibling::P[@Name='DstPort']")
						.getText();
				Element dstcmp = (Element) document.selectSingleNode("//Model/System/Block[@Name='" + blockname + "']");

				portList = document.selectNodes(
						dstcmp.getUniquePath() + "/System/Block[@BlockType='Inport' or @BlockType='Outport']");
				GetPortByID(document, cchannel, port, portList, "dest");
				insert_cchannel(cchannel);
			}
		}
	}

	public void GetPortByID(Document document, communicationchannel cchannel, String port, List<Node> portList,
			String portType) {
		if (port.equals("1")) {
			for (Node n2 : portList) {
				Element portelem = (Element) document.selectSingleNode(n2.getUniquePath() + "/P[@Name='Port']");
				if (portelem == null) {
					if (portType.equals("source")) {
						cchannel.setSourceid(Integer.valueOf(((Element) n2).attributeValue("id")));
					} else {
						cchannel.setDestid(Integer.valueOf(((Element) n2).attributeValue("id")));
					}
				}
			}
		} else {
			for (Node n2 : portList) {
				Element portelem = (Element) document.selectSingleNode(n2.getUniquePath() + "/P[@Name='Port']");
				if (portelem != null && portelem.getText().equals(port)) {
					// 端口的数据
//					Element portdata = (Element) document.selectSingleNode(
//							"//chart/Children/data[@name='" + ((Element) n2).attributeValue("Name") + "']");
					if (portType.equals("source")) {
						cchannel.setSourceid(Integer.valueOf(((Element) n2).attributeValue("id")));
					} else {
						cchannel.setDestid(Integer.valueOf(((Element) n2).attributeValue("id")));
					}
				}
			}
		}
	}

	public Integer getCmpIDbyName(String name) {
		return camArchMapper.getIDbyName(name).getComponentid();
	}

	private Map<String, String> getstateinfo(String modelfilename, Element transition, String ssid) throws Exception {
		Document document = ModelResolver(modelfilename);
		Element pElement = (Element) document.selectSingleNode(
				transition.getParent().getUniquePath() + "/state[@SSID='" + ssid + "']/P[@Name='labelString']");
		Map<String, String> r = new HashMap<>();
		r.put("id", pElement.getParent().attributeValue("id"));
		String[] strings = pElement.getText().split("\n");
		for (String s : strings) {
			if (s.contains("entry")) {

				r.put("trigger", s.split(":")[1]);
			}
		}
		return r;
	}
}

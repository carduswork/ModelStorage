package extractor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
						// TODO 组件的wcet待确认

					}
				}
			}
			insert_component(c);

			LinkpointResolver(filepath, n.getUniquePath(), c);
			TaskResolver(filepath, c);
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
			//端口的数据
			Element portdata = (Element) document.selectSingleNode(
					"//chart/Children/data[@name='" + element2.attributeValue("Name") + "']");
			//TODO 等那俩二百五更新xml文件后调试一下
//			String period=document.selectSingleNode(portdata.getUniquePath()+"/P[@Name='description']").getText();
//			ports1.setPeriod(period);
//			dataobject d=new dataobject();
			
			
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
			insert_ports(ports1);

			_require req = new _require();
			req.setRequirer(father.getComponentid());
			req.setRequired(linkpointID);
			insert_require(req);
		}
		// 寻找linkpoint的属性

	}

	private void TaskResolver(String modelfilename, component father) throws Exception {
		Document document = ModelResolver(modelfilename);
		String gettask = "//chart/P[contains(text(),'" + father.getName() + "')]/Children/state";
		List<? extends Node> namelist = document.selectNodes(gettask);
		for (Node n : namelist) {
			Element element2 = (Element) n;
			component component = new component();
			Integer idString = (int) GetID.getId();
			component.setComponentid(idString);

			component.setModeltype("simulink");
			Element ls = (Element) document.selectSingleNode(element2.getUniquePath() + "/P[@Name='labelString']");
			component.setName(ls.getText());
			component.setType("task");
			insert_component(component);
			_task t = new _task();
			t.setName(ls.getText());
			t.setTaskid(idString);
			try {
				AppendID.AppendID(modelfilename, element2.getUniquePath(), t.getTaskid().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
//			tasklist.add(t);
			insert_task(t);
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
			AppendID.AppendID(modelfilename, n.getUniquePath(), idString.toString());

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
					GetPortByID(document, cchannel, port, portList, "dest");

					insert_cchannel(cchannel);
				}

			} else {

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
					//端口的数据
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
}

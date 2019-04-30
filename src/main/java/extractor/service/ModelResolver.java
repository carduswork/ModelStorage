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

import extractor.model.ASyncMessaging;
import extractor.model.Device;
import extractor.model.DispatchChannel;
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
import extractor.model.connections;
import extractor.model.linkpoint;
import extractor.model.rtos;
import extractor.model.shareddataaccess;
import extractor.model.syncinterface;
import extractor.model.transition;
import extractor.model.transitionstate;
import javassist.expr.NewArray;

//解析模型,本模块只负责模型文件的解析
//@Service("ModelResolver")
public class ModelResolver {
	@Autowired
	private static ModelService ms;
	static List<? extends Node> components = null;
	static List<? extends Node> cchannels = null;
	static Map<String, component> componentlist = new HashMap<String, component>();
	static List<_task> tasklist = new ArrayList<_task>();

	static List<linkpoint> portlist = new ArrayList<linkpoint>();
	static List<_require> requirelist = new ArrayList<_require>();
	static List<_provide> providelist = new ArrayList<_provide>();

	static List<_exception> exceptionlist = new ArrayList<_exception>();
	static List<Device> devicelist = new ArrayList<Device>();
	static List<bus> buslist = new ArrayList<bus>();
	static List<rtos> rtoslist = new ArrayList<rtos>();

	static List<shareddataaccess> sdalist = new ArrayList<shareddataaccess>();
	static List<syncinterface> synclist = new ArrayList<syncinterface>();
	static List<ASyncMessaging> ASMlist = new ArrayList<ASyncMessaging>();

	static List<communicationchannel> cclist = new ArrayList<communicationchannel>();
	static List<InvocationChannel> ivclist = new ArrayList<InvocationChannel>();
	static List<DispatchChannel> dpclist = new ArrayList<DispatchChannel>();
	static List<MessageChannel> mclist = new ArrayList<MessageChannel>();

	static List<_event> eventlist = new ArrayList<_event>();
	static List<_state> statelist = new ArrayList<_state>();

	static List<transition> tslist = new ArrayList<transition>();
	static List<transitionstate> ts_slist = new ArrayList<transitionstate>();

	public static Document ModelResolver(String url) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		return document;
	}

//filename参数是hardwareArchitecture
	public static void MatchCChannel(String modelfilename, String modelType) throws Exception {
		Document document = ModelResolver(modelfilename);
		List<String> namelist = new ArrayList<>();
		if (modelType.equals("aadl")) {
			String getCChannel = "";
			// hardware层的cchannel解析
			// bus access或者叫asyncmessaging
			String getmessagechannel = "//ownedPublicSection/ownedClassifier[@xsi:type='aadl2:SystemImplementation']/ownedAccessConnection";
			cchannels = document.selectNodes(getmessagechannel);
			ResolveCChannel(modelfilename, "asyncmessaging");
			// syncinterface
			String getsync = "//ownedPublicSection/ownedClassifier[@xsi:type='aadl2:SystemImplementation']/ownedPortConnection";
			cchannels = document.selectNodes(getsync);
			ResolveCChannel(modelfilename, "sync");
			// dispatchChannel
			// TODO 修改案例完成dispatchChannel的解析xpath
			String getdc = "//ownedPublicSection/";
			cchannels = document.selectNodes(getdc);
			ResolveCChannel(modelfilename, "dispatchChannel");
		}
	}

	private static void ResolveCChannel(String modelfilename, String t) throws Exception {
		for (Node n : cchannels) {
			Element element = (Element) n;
			communicationchannel cchannel = new communicationchannel();
			Integer idString = (int) GetID.getId();
			cchannel.setName(element.attributeValue("name"));
			cchannel.setCommunicationchannelid(idString);

			switch (t) {
			case "asyncmessaging":
				// TODO 解析dispatch的source,dest
				// TODO 解析inbuffer,outbuffer
				// context是目标元素在本文件中的路径
				// connectionEnd是连了哪个端口
				// source有context是组件向bus发送，没有context是组件接收bus的数据
				if (element.element("source").attribute("context") != null) {
					// inbuffer
					// 解析source
					// 端口会重名但是组件名不会，先找组件名再找组件名下的端口id
					String CompositeName = GetName(modelfilename, element.element("source").attributeValue("context"));
					// 这里是测试用把路径写死
					String PortName = GetName("src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2",
							element.element("source").attributeValue("connectionEnd"));
					Integer sourceportid = ms.getPortIDByComponentName(CompositeName, PortName);
					cchannel.setSourceid(sourceportid);
				} else {
					// outbuffer
					// 此时source是bus
					String busname = GetName(modelfilename, element.element("source").attributeValue("connectionEnd"));
					Integer busid = ms.getCmpNameByID(busname);
					cchannel.setSourceid(busid);
				}
				// dest没有context是组件向bus发送，有context是组件接收bus的数据
				if (element.element("destination").attribute("context") == null) {
					// inbuffer
					// dest是bus
					String busname = GetName(modelfilename,
							element.element("destination").attributeValue("connectionEnd"));
					Integer busid = ms.getCmpNameByID(busname);
					cchannel.setDestid(busid);
				} else {
					// outbuffer
					// dest是端口
					String CompositeName = GetName(modelfilename,
							element.element("destination").attributeValue("context"));
					// 这里是测试用把路径写死
					String PortName = GetName("src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2",
							element.element("destination").attributeValue("connectionEnd"));
					Integer destportid = ms.getPortIDByComponentName(CompositeName, PortName);
					cchannel.setDestid(destportid);
				}
				cchannel.setType("asyncmessaging");
				MessageChannel b = new MessageChannel();
				b.setMessagechannelid(idString);
				mclist.add(b);
				break;
			case "sync":
				String CompositeName = GetName(modelfilename, element.element("source").attributeValue("context"));
				// 这里是测试用把路径写死
				String PortName = GetName("src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2",
						element.element("source").attributeValue("connectionEnd"));
				Integer sourceportid = ms.getPortIDByComponentName(CompositeName, PortName);
				cchannel.setSourceid(sourceportid);

				String CompositeName1 = GetName(modelfilename,
						element.element("destination").attributeValue("context"));
				// 这里是测试用把路径写死
				String PortName1 = GetName("src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2",
						element.element("destination").attributeValue("connectionEnd"));
				Integer destportid = ms.getPortIDByComponentName(CompositeName1, PortName1);
				cchannel.setDestid(destportid);

				cchannel.setType("sync");
				InvocationChannel r = new InvocationChannel();
				r.setInvocationchannelid(idString);
				ivclist.add(r);
				break;
			case "dispatchChannel":
				cchannel.setType("dispatchChannel");
				DispatchChannel d = new DispatchChannel();
				d.setDispatchchannelid(idString);
				dpclist.add(d);
				break;
			}
			cclist.add(cchannel);
		}
	}

//解析source路径,传递context，跨文件查找
	private static String GetName(String modelfilename, String path) throws Exception {
		Document document = ModelResolver(modelfilename);
		path = GetXPath(path);
		Node node = document.selectSingleNode(path);
		Element e = (Element) node;
		return e.attributeValue("name");
	}

	/*
	 * 根据路径获取ID,只支持三层结构的xpath
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

			result.set(j, getinc(result.get(j)));
		}
		return "//" + result.get(0) + "/" + result.get(1) + "]/" + result.get(2) + "]";
	}

	private static String GetXPath4State(String path) {
		String reg = "(?<=@).[A-Za-z0-9\\.]+";
		ArrayList<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			result.add(matcher.group());
		}
		for (int j = 1; j < result.size(); j++) {

			result.set(j, getinc(result.get(j)));
		}
		return "//" + result.get(0) + "/" + result.get(1) + "/" + result.get(2) + "/" + result.get(3) + "/"
				+ result.get(4);
	}

	private static String getinc(String source) {
//		String csub = source.substring(0, source.length() - 2);
//		String intsub = source.substring(source.length() - 1);
//		intsub = String.valueOf(Integer.parseInt(intsub) + 1);
		CharSequence cs = ".";
		if (source.contains(cs)) {
			String reg4c = "[A-Za-z]+";
			String reg4num = "[0-9]+";

			Pattern pattern = Pattern.compile(reg4c);
			Matcher matcher = pattern.matcher(source);
			List<String> list = new ArrayList<>();
			while (matcher.find()) {
				list.add(matcher.group());
			}
			String c = list.get(0);

			pattern = Pattern.compile(reg4num);
			matcher = pattern.matcher(source);
			list = new ArrayList<>();
			while (matcher.find()) {
				list.add(matcher.group());
			}
			Integer i = Integer.valueOf(list.get(0));
			String num = (++i).toString();

			return c + "[" + num + "]";
		} else {
			return source;
		}

	}

	// 当前模型的全部组件
	/* 与port */
	public static void MatchComponents(String modelfilename, String modelType, String contenttype) throws Exception {

		Document document = ModelResolver(modelfilename);

		List<String> namelist = new ArrayList<>();

		if (modelType.equals("aadl")) {
			// 查找作为进程的components

			String innerProcess = "//ownedClassifier[@xsi:type='aadl2:ProcessType']";
			String innerThread = "//ownedClassifier[@xsi:type='aadl2:ThreadType']";
			String innerDevcvice = "//ownedClassifier[@xsi:type='aadl2:ProcessorType'or @xsi:type='aadl2:MemoryType']";

			// 对hardware层级设计的三种与组件元素的解析
			String getbus = "//ownedClassifier[@xsi:type='aadl2:SystemImplementation/ownedBusSubcomponent']";
			String getsys = "//ownedClassifier[@xsi:type='aadl2:SystemImplementation/ownedSystemSubcomponent']";
			String getdevice = "//ownedClassifier[@xsi:type='aadl2:SystemImplementation/ownedDeviceSubcomponent']";
			// TODO task的端口映射，父子包含关系的体现
			switch (contenttype) {
			case "系统内部结构":
				// TODO 把之前kf系统内部的解析规则加载进来，遍历边界上的端口、进程线程、process及memory子组件
				// TODO 关联state
				components = document.selectNodes(innerProcess);				
				TaskResolver(modelfilename,innerProcess);
				
				components = document.selectNodes(innerThread);
				TaskResolver(modelfilename,innerThread);
				
				components = document.selectNodes(innerDevcvice);
				ResolveComponents("device");
				break;
			case "总体架构":
				components = document.selectNodes(getbus);
				ResolveComponents("bus");
				components = document.selectNodes(getsys);
				ResolveComponents("sys");
				components = document.selectNodes(getdevice);
				ResolveComponents("device");
				break;
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

	private static void ResolveComponents(String t) throws Exception {
		List<? extends Node> ports = null;
		for (Node n : components) {
			Element element = (Element) n;
			component component = new component();
			Integer idString = (int) GetID.getId();
			component.setModeltype("aadl");
			component.setName(element.attributeValue("name"));
			component.setComponentid(idString);
			switch (t) {
			case "bus":
				component.setType("bus");
				bus b = new bus();
				b.setBusid(idString);
				buslist.add(b);
				break;
			case "sys":
				component.setType("system");
				rtos r = new rtos();
				r.setRtosid(idString);
				rtoslist.add(r);
				break;
			case "device":
				component.setType("device");
				Device d = new Device();
				d.setDeviceid(idString);
				devicelist.add(d);
				break;
			}
			componentlist.put(element.attributeValue("name"), component);
			// 名下的linkpoint
			PortResolver(ports, idString, t, element.attributeValue("name"));
		}
	}

	private static void PortResolver(List<? extends Node> ports, Integer idString, String t, String componetname)
			throws Exception {
		String filetofind = "";
		switch (t) {
		case "bus":
		case "device":
			filetofind = "Composition.aaxl2";
			Document document = ModelResolver("src/main/resources/modelresource/JH_FK/packages/" + filetofind);
			// 构建linkpoint,分开处理busaccess与dataport,eventport,busaccess看require属性,dataport与之前相同

			// bussaccess
			ports = document
					.selectNodes("//ownedPublicSection/ownedClassifier[@name='" + componetname + "']/ownedBusAccess");
			TraverseOwnedPorts(ports, idString, "busaccess");
			// dataport
			ports = document
					.selectNodes("//ownedPublicSection/ownedClassifier[@name='" + componetname + "']/ownedDataPort");
			TraverseOwnedPorts(ports, idString, "dataport");
			// eventport
			ports = document
					.selectNodes("//ownedPublicSection/ownedClassifier[@name='" + componetname + "']/ownedEventPort");
			// 对应关系，busaccess对应----- eventport对应------ dataport对应--------
			TraverseOwnedPorts(ports, idString, "eventport");
			break;
		case "sys":
			filetofind = "kfBefore.aaxl2";
			Document document2 = ModelResolver("src/main/resources/modelresource/JH_FK/packages/" + filetofind);
			// 同上
			ports = document2.selectNodes("//ownedClassifier[@xsi:type='aadl2:SystemType']/ownedDataPort");
			TraverseOwnedPorts(ports, idString, "dataport");
			ports = document2.selectNodes("//ownedClassifier[@xsi:type='aadl2:SystemType']/ownedEventPort");
			TraverseOwnedPorts(ports, idString, "eventport");
			ports = document2.selectNodes("//ownedClassifier[@xsi:type='aadl2:SystemType']/ownedBusAccess");
			TraverseOwnedPorts(ports, idString, "busaccess");
			break;
		}
	}

	private static void TraverseOwnedPorts(List<? extends Node> ports, Integer idString, String portType) {
		for (Node n2 : ports) {
			Element element2 = (Element) n2;
			linkpoint ports1 = new linkpoint();
			// 暂时只设置name这一个
			ports1.setName(element2.attributeValue("name"));
			Integer linkpointID = (int) GetID.getId();
			// TODO taskschedule的创建入库
			switch (portType) {
			case "dataport":
			case "eventport":
				syncinterface si = new syncinterface();
				si.setSyncinterfaceid(linkpointID);

				if (!(element2.attribute("in") == null)) {
					// 是输入端口
					_require r = new _require();
					r.setRequired(linkpointID);
					r.setRequirer(idString);
					requirelist.add(r);
				} else {
					_provide p = new _provide();
					p.setProvided(linkpointID);
					p.setProvider(idString);
					providelist.add(p);
				}
				break;
			case "busaccess":
				ASyncMessaging asm = new ASyncMessaging();
				asm.setAsyncmessagingid(linkpointID);
				if (element2.attribute("kind").equals("requires")) {
					// 是输入端口
					_require r = new _require();
					r.setRequired(linkpointID);
					r.setRequirer(idString);
					requirelist.add(r);
				} else {
					_provide p = new _provide();
					p.setProvided(linkpointID);
					p.setProvider(idString);
					providelist.add(p);
				}
				break;
			}
			ports1.setLinkpointid(linkpointID);
			portlist.add(ports1);
		}
	}

	// 错误附件的解析
	private static void ExceptionResolver(String modelfilename, String modelType) throws Exception {
		Document document = ModelResolver(modelfilename);

		List<? extends Node> nodelist = new ArrayList<>();
		if (modelType.equals("aadl")) {
			// 读取system节点
			nodelist = document.selectNodes("//ownedClassifier[@xsi:type='aadl2:SystemType']");
			nodelist.forEach((v) -> {

				Element element = (Element) v;
				String compositename = element.attributeValue("name");

				_exception e = new _exception();
				e.setName(element.element("ownedAbstractFeature").attributeValue("name"));
				String exceptionTypeString = getType(
						element.element("ownedAnnexSubclause").element("parsedAnnexSubclause").element("propagations")
								.element("typeSet").element("typeTokens").attributeValue("type"));
				e.setType(exceptionTypeString);
				element = element.element("ownedAnnexSubclause").element("parsedAnnexSubclause")
						.element("propagations");
				Integer sourceid = 0, destid = 0;
				// 没有direction或者
				if (element.attributeValue("direction").equals("out")) {
					String getsourceportPath = element.element("featureorPPRef").attributeValue("featureorPP");
					try {
						sourceid = ms.getPortIDByComponentName(compositename,
								GetName(modelfilename, GetXPath(getsourceportPath)));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else {
					String getsourceportPath = element.element("featureorPPRef").attributeValue("featureorPP");
					try {
						destid = ms.getPortIDByComponentName(compositename,
								GetName(modelfilename, GetXPath(getsourceportPath)));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				e.setCommunicationchannelid(ms.getCChannelBysd(sourceid, destid));
				exceptionlist.add(e);
			});

		}
	}

	public static void TransitionResolver(String modelfilename, String modelType) throws Exception {
		Document document = ModelResolver(modelfilename);
		List<? extends Node> transNodes = document
				.selectNodes("//ownedAnnexSubclause/parsedAnnexSubclause/transitions");

		transNodes.forEach((v) -> {
			Element element = (Element) v;
			// transition与作为trigger的event关联
			String getTrigger = element.element("condition").element("qualifiedErrorPropagationReference")
					.element("emv2Target").attributeValue("namedElement");

			transition e2 = new transition();
			try {
				e2.setTransitionid((int) GetID.getId());
				e2.setTrigger(ms.getEventID(GetName(modelfilename, GetXPath4State(getTrigger))));
				e2.setName(getType(element.attributeValue("type")));
				transitionstate tsTransitionstate = new transitionstate();
				tslist.add(e2);
				tsTransitionstate.setSource(
						ms.getStateID(GetName("src/main/resources/modelresource/JH_FK/packages/ErrorLib.aaxl2",
								GetXPath4State(element.attributeValue("source")))));
				tsTransitionstate
						.setOut(ms.getStateID(GetName("src/main/resources/modelresource/JH_FK/packages/ErrorLib.aaxl2",
								GetXPath4State(element.attributeValue("target")))));

				tsTransitionstate.setTransitionid(e2.getTransitionid());
			} catch (Exception e) {
				e.printStackTrace();
			}

		});
	}

	public static void eventResolver(String modelfilename, String modelType) throws Exception {
		// TODO 下一个event的确定
		// 解析错误附件定义的event
		Document document = ModelResolver(modelfilename);
		List<? extends Node> erroreventName = document.selectNodes("//ownedAnnexSubclause/parsedAnnexSubclause/events");
		erroreventName.forEach((v) -> {
			Element element = (Element) v;
			_event e2 = new _event();
			e2.setName(getType(element.attributeValue("name")));
			eventlist.add(e2);
		});
	}

	public static void StateResolver(String modelfilename, String modelType) throws Exception {
		// TODO state与interface的关联，state考虑aadl定义状态的方式，component的state可能扫描不全
		Document document = ModelResolver(modelfilename);

		List<? extends Node> stateInfo = document
				.selectNodes("//ownedAnnexSubclause/parsedAnnexSubclause/states/condition/operands/operands");
		stateInfo.forEach((v) -> {
			Element element = (Element) v;
			String statenameString = element.element("qualifiedState").attributeValue("state");

			String cmpID = element.element("qualifiedState").element("subcomponent").attributeValue("subcomponent");
			_state s = new _state();
			try {
				s.setComponentid(ms.getCmpNameByID(
						GetName("src/main/resources/modelresource/JH_FK/packages/HardwareArchitecture.aaxl2",
								GetXPath(cmpID))));
				// TODO 后面改成输入的错误库文件
				s.setName(GetName("src/main/resources/modelresource/JH_FK/packages/ErrorLib.aaxl2",
						GetXPath4State(statenameString)));
			} catch (Exception e) {
			}
			statelist.add(s);
		});
	}

	// 获取Exception的类型
	private static String getType(String typepath) {
		String[] s = typepath.split(".");
		return s[s.length - 1];
	}

	private static void TaskResolver(String modelfilename,String taskpath) {
		Document document = ModelResolver(modelfilename);
		
		
		_task t = new _task();
		t.setTaskid(taskid);
	}
}

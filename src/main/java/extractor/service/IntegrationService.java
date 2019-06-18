package extractor.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import extractor.DAO.mapper._exceptionMapper;
import extractor.DAO.mapper._partitionMapper;
import extractor.DAO.mapper._provideMapper;
import extractor.DAO.mapper._requireMapper;
import extractor.DAO.mapper._stateMapper;
import extractor.DAO.mapper._taskMapper;
import extractor.DAO.mapper.communicationchannelMapper;
import extractor.DAO.mapper.componentMapper;
import extractor.DAO.mapper.linkpointMapper;
import extractor.DAO.mapper.processorMapper;
import extractor.model._partition;
import extractor.model._state;
import extractor.model._task;
import extractor.model.communicationchannel;
import extractor.model.component;
import extractor.model.linkpoint;

//获取各种映射
@Service("Map")
public class IntegrationService {

	@Autowired
	private componentMapper cm;
	@Autowired
	private linkpointMapper lm;
	@Autowired
	private _stateMapper sm;
	@Autowired
	private _taskMapper _tm;
	@Autowired
	private communicationchannelMapper cchannelMapper;
	@Autowired
	private processorMapper processorm;
	@Autowired
	private _partitionMapper ptnm;
	@Autowired
	private _exceptionMapper em;
	@Autowired
	private _provideMapper pvm;
	@Autowired
	private _requireMapper rm;

	public static Document ModelResolver(String url) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);
		return document;
	}

	public void GenerateIntegaraton(String filename, String modeltype) {
		String dir = "src/main/resources/INTEGRATIONMODEL/";
		// 可配置
		try {
			Document d2 = DocumentHelper.createDocument();
			// 设置系统名
			Element e_comp = d2.addElement("ownedPublicSection");
			e_comp.addAttribute("name", filename);
			List<component> r = new ArrayList<component>();
			// 设置组件
			switch (modeltype) {
			case "aadl":
				r = cm.selectAll_aadl();
				break;
			case "sysml":
				r = cm.selectAll_sysml();
				break;
			case "simulink":
				r = cm.selectAll_slk();
				break;
			}

			r.forEach((compv) -> {
				Element comp = e_comp.addElement("component");
				comp.addAttribute("name", compv.getName());
				comp.addAttribute("id", compv.getComponentid().toString());
				comp.addAttribute("type", compv.getType());

				// 设置linkpoint,transition
				List<linkpoint> linpoints = lm.getPortUnderCMP(compv.getComponentid());
				linpoints.forEach((v2) -> {
					Element lp = comp.addElement("linkpoint");
					lp.addAttribute("name", v2.getName());
					lp.addAttribute("id", v2.getLinkpointid().toString());
					if (pvm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "out");
					}
					if (rm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "in");
					}
				});
				// 设置state
				List<_state> states = sm.getStateUnderCMP(compv.getComponentid());
				states.forEach((v3) -> {
					if (v3 != null) {
						Element lp = comp.addElement("state");
						lp.addAttribute("name", v3.getName());
						lp.addAttribute("id", v3.getStateid().toString());
					}
				});
				// 设置processor即partition
				List<_partition> processorlist = ptnm.selectByRTOS(compv.getComponentid());
				processorlist.forEach((v5) -> {
					Element psr = comp.addElement("partition");
					psr.addAttribute("id", v5.getPartitionid().toString());
					// 设置task
					List<_task> tasklist = _tm.selectBypartition(v5.getPartitionid());
					tasklist.forEach((v4) -> {
						Element tsk = psr.addElement("task");
						tsk.addAttribute("Name", v4.getName());
						tsk.addAttribute("id", v4.getTaskid().toString());
						tsk.addAttribute("deadline", v4.getDeadline());
						tsk.addAttribute("period", v4.getPeriod());
						tsk.addAttribute("wcet", v4.getWcet());

						List<linkpoint> processports = lm.getPortUnderCMP(v4.getTaskid());
						processports.forEach((v7) -> {
							Element lp = tsk.addElement("port");
							lp.addAttribute("name", v7.getName());
							lp.addAttribute("id", v7.getLinkpointid().toString());
							if (pvm.selectByportid(v7.getLinkpointid()) != null) {
								lp.addAttribute("direction", "out");
							}
							if (rm.selectByportid(v7.getLinkpointid()) != null) {
								lp.addAttribute("direction", "in");
							}
						});

						List<_task> childtasklist = _tm.selectChild(v4.getTaskid());
						childtasklist.forEach((v6) -> {
							Element child = tsk.addElement("task");
							child.addAttribute("Name", v6.getName());
							child.addAttribute("id", v6.getTaskid().toString());
							child.addAttribute("deadline", v6.getDeadline());
							child.addAttribute("period", v6.getPeriod());
							child.addAttribute("wcet", v6.getWcet());

							List<linkpoint> threadports = lm.getPortUnderCMP(v6.getTaskid());
							threadports.forEach((v8) -> {
								Element lp = child.addElement("port");
								lp.addAttribute("name", v8.getName());
								lp.addAttribute("id", v8.getLinkpointid().toString());
								if (pvm.selectByportid(v8.getLinkpointid()) != null) {
									lp.addAttribute("direction", "out");
								}
								if (rm.selectByportid(v8.getLinkpointid()) != null) {
									lp.addAttribute("direction", "in");
								}
							});
						});

					});
				});
				//设置不在partition上的task
				List<_task> tasklist = _tm.selectChild(compv.getComponentid());
				tasklist.forEach((taskv)->{
					Element tsk = comp.addElement("task");
					tsk.addAttribute("Name", taskv.getName());
					tsk.addAttribute("id", taskv.getTaskid().toString());
					tsk.addAttribute("deadline", taskv.getDeadline());
					tsk.addAttribute("period", taskv.getPeriod());
					tsk.addAttribute("wcet", taskv.getWcet());
				});
			});
			// 设置cchannel
			List<communicationchannel> channellist = cchannelMapper.getAll();
			channellist.forEach((v) -> {
				Element e = e_comp.addElement("communicationchannel");
				e.addAttribute("name", v.getName());
				e.addAttribute("id", v.getCommunicationchannelid().toString());
				e.addAttribute("type", v.getType());
				e.addAttribute("source", String.valueOf(v.getSourceid()));
				e.addAttribute("dest", String.valueOf(v.getDestid()));
			});

			OutputStream outStream = new FileOutputStream(dir + filename);
			XMLWriter w = new XMLWriter(outStream);
			w.write(d2);
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	按照映射表来搜索
	private void filterbymap() {

	}
}

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

import antlr.debug.Event;
import extractor.DAO.mapper._eventMapper;
import extractor.DAO.mapper._exceptionMapper;
import extractor.DAO.mapper._partitionMapper;
import extractor.DAO.mapper._provideMapper;
import extractor.DAO.mapper._requireMapper;
import extractor.DAO.mapper._stateMapper;
import extractor.DAO.mapper._taskMapper;
import extractor.DAO.mapper.communicationchannelMapper;
import extractor.DAO.mapper.componentMapper;
import extractor.DAO.mapper.componenttransitionMapper;
import extractor.DAO.mapper.connectionsMapper;
import extractor.DAO.mapper.dataobjectMapper;
import extractor.DAO.mapper.linkpointMapper;
import extractor.DAO.mapper.processorMapper;
import extractor.DAO.mapper.slkstateMapper;
import extractor.DAO.mapper.transitionMapper;
import extractor.DAO.mapper.transitionstateMapper;
import extractor.model._event;
import extractor.model._exception;
import extractor.model._partition;
import extractor.model._state;
import extractor.model._task;
import extractor.model.communicationchannel;
import extractor.model.component;
import extractor.model.componenttransition;
import extractor.model.connections;
import extractor.model.dataobject;
import extractor.model.linkpoint;
import extractor.model.slkstate;
import extractor.model.transition;
import extractor.model.transitionstate;

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
	private transitionMapper tsm;
	@Autowired
	private transitionstateMapper tssm;
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
	@Autowired
	private _eventMapper evtm;
	@Autowired
	private componenttransitionMapper cttm;
	@Autowired
	private slkstateMapper slksm;
	@Autowired
	private dataobjectMapper dm;
	@Autowired
	private connectionsMapper cnm;

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
			Element e_comp = d2.addElement("system");
			e_comp.addAttribute("name", cm.getByType("uniquesystem").getName());
			e_comp.addAttribute("wcet", cm.getByType("uniquesystem").getWcet());
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
				comp.addAttribute("wcet", compv.getWcet());
				// 设置linkpoint,transition
				List<linkpoint> linpoints = lm.getPortUnderCMP(compv.getComponentid());
				linpoints.forEach((v2) -> {
					Element lp = comp.addElement("linkpoint");
					lp.addAttribute("name", v2.getName());
					lp.addAttribute("id", v2.getLinkpointid().toString());
					lp.addAttribute("period", v2.getPeriod());
					if (pvm.selectByportid(v2.getLinkpointid()) != null
							&& rm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "inout");
					} else if (pvm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "out");
					} else if (rm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "in");
					}
					if (dm.getByFrom(v2.getLinkpointid()) != null) {

						lp.addAttribute("datatype", dm.getByFrom(v2.getLinkpointid()).getDatatype());
					}
				});
				List<_exception> ecplist = em.selectByComp(compv.getComponentid());
				ecplist.forEach((ecpv) -> {
					Element ecpelement = comp.addElement("propagation");
					ecpelement.addAttribute("name", ecpv.getName());
					ecpelement.addAttribute("fault", ecpv.getType());
					ecpelement.addAttribute("id", ecpv.getExceptionid().toString());
					if (modeltype.equals("aadl")) {

						ecpelement.addAttribute("port_id", ecpv.getLinkpointid().toString());
					}
				});
				List<_state> sList = sm.getStateUnderCMP(compv.getComponentid());
				for (_state state : sList) {
					Element stateElement = comp.addElement("state");
					stateElement.addAttribute("id", state.getStateid().toString());
					stateElement.addAttribute("name", state.getName());
				}
				// transition绑定component
				List<componenttransition> ct = cttm.selectByComponent(compv.getComponentid());
				ct.forEach((ctv) -> {
					List<transitionstate> tsslist = tssm.selectbytransition(Integer.valueOf(ctv.getTransitionid()));
					tsslist.forEach((vtss) -> {
						// 设置transition
						transition ts = tsm.selectByPrimaryKey(vtss.getTransitionid());
						Element transitionElement = comp.addElement("transition");
						transitionElement.addAttribute("id", ts.getTransitionid().toString());

						_state d = sm.selectByPrimaryKey(vtss.getOutid());
						transitionElement.addAttribute("dest", d.getStateid().toString());
						_event event = evtm.selectByPrimaryKey(ts.getTriggerid());

						transitionElement.addAttribute("event", event.getName());

						_state s = sm.selectByPrimaryKey(vtss.getSourceid());
						transitionElement.addAttribute("source", s.getStateid().toString());
					});
				});
				// 设置processor即partition
				List<_partition> processorlist = ptnm.selectByRTOS(compv.getComponentid());
				processorlist.forEach((v5) -> {
					Element psr = comp.addElement("partition");
					psr.addAttribute("id", v5.getPartitionid().toString());
					// 设置task
					List<_task> tasklistinpart = new ArrayList<_task>();
					tasklistinpart = _tm.selectBypartition(v5.getPartitionid());
					tasklistinpart.forEach((taskv) -> {
						Element tsk = psr.addElement("task");
						tsk.addAttribute("Name", taskv.getName());
						tsk.addAttribute("id", taskv.getTaskid().toString());
						tsk.addAttribute("deadline", taskv.getDeadline());
						tsk.addAttribute("period", taskv.getPeriod());
						tsk.addAttribute("wcet", taskv.getWcet());

						List<linkpoint> taskports = lm.getPortUnderCMP(taskv.getTaskid());
						taskports.forEach((v7) -> {
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
						// 设置thread
						List<_task> childtasklist = _tm.selectChild(taskv.getTaskid());
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
						// partition上thread的连接
						List<connections> connectionlist = cnm.selectByfather(taskv.getTaskid());
						connectionlist.forEach((c) -> {
							Element e = tsk.addElement("connection");
							e.addAttribute("source", c.getStartcomponentid().toString());
							e.addAttribute("dest", c.getEndcomponentid().toString());
							e.addAttribute("id", c.getIdconnections().toString());
							e.addAttribute("name", c.getConnectiontype());
						});
					});
					// partition上 process的连接
					List<connections> threadconnectionlist = cnm.selectByfather(v5.getPartitionid());
					threadconnectionlist.forEach((threadconnection) -> {
						Element e = psr.addElement("connection");
						e.addAttribute("source", threadconnection.getStartcomponentid().toString());
						e.addAttribute("dest", threadconnection.getEndcomponentid().toString());
						e.addAttribute("id", threadconnection.getIdconnections().toString());

					});
				});
				// 设置不在partition上的task
				List<_task> tasklist = _tm.selectChildnotinpart(compv.getComponentid());
				tasklist.forEach((taskv) -> {
					Element tsk = comp.addElement("task");
					tsk.addAttribute("Name", taskv.getName());
					tsk.addAttribute("id", taskv.getTaskid().toString());
					tsk.addAttribute("deadline", taskv.getDeadline());
					tsk.addAttribute("period", taskv.getPeriod());
					tsk.addAttribute("wcet", taskv.getWcet());
					// 不在partition的thread
					List<linkpoint> threadports = lm.getPortUnderCMP(taskv.getTaskid());
					threadports.forEach((v8) -> {
						Element lp = tsk.addElement("port");
						lp.addAttribute("name", v8.getName());
						lp.addAttribute("id", v8.getLinkpointid().toString());
						if (pvm.selectByportid(v8.getLinkpointid()) != null) {
							lp.addAttribute("direction", "out");
						}
						if (rm.selectByportid(v8.getLinkpointid()) != null) {
							lp.addAttribute("direction", "in");
						}
					});
					// 不在partition上的thread
					List<_task> threadlist = _tm.selectChild(taskv.getTaskid());
					threadlist.forEach((threadv) -> {
						Element child = tsk.addElement("task");
						child.addAttribute("Name", threadv.getName());
						child.addAttribute("id", threadv.getTaskid().toString());
						child.addAttribute("deadline", threadv.getDeadline());
						child.addAttribute("period", threadv.getPeriod());
						child.addAttribute("wcet", threadv.getWcet());

						List<linkpoint> threadports2 = lm.getPortUnderCMP(threadv.getTaskid());
						threadports2.forEach((v8) -> {
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
						// 不在partition上的thread的连接
						List<connections> connectionlist = cnm.selectByfather(threadv.getTaskid());
						connectionlist.forEach((c) -> {
							Element e = tsk.addElement("connection");
							e.addAttribute("source", c.getStartcomponentid().toString());
							e.addAttribute("dest", c.getEndcomponentid().toString());
							e.addAttribute("id", c.getIdconnections().toString());
							e.addAttribute("name", c.getConnectiontype());
						});
					});
				});
				// 设置不在partition上的task间的连接
				List<connections> connectionlist = cnm.selectByfather(compv.getComponentid());
				connectionlist.forEach((c) -> {
					Element e = comp.addElement("connection");
					e.addAttribute("source", c.getStartcomponentid().toString());
					e.addAttribute("dest", c.getEndcomponentid().toString());
					e.addAttribute("id", c.getIdconnections().toString());

				});
			});
			// 设置cchannel
			List<communicationchannel> channellist = cchannelMapper.getAll(modeltype);
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

	public void GenerateIntegaraton4SLK(String filename, String modeltype) {
		String dir = "src/main/resources/INTEGRATIONMODEL/";
		// 可配置
		try {
			Document d2 = DocumentHelper.createDocument();
			// 设置系统名
			Element e_comp = d2.addElement("system");
			e_comp.addAttribute("name", cm.getByType("uniquesystem").getName());
			e_comp.addAttribute("wcet", cm.getByType("uniquesystem").getWcet());
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
				comp.addAttribute("wcet", compv.getWcet());
				// 设置linkpoint,transition
				List<linkpoint> linpoints = lm.getPortUnderCMP(compv.getComponentid());
				linpoints.forEach((v2) -> {
					Element lp = comp.addElement("linkpoint");
					lp.addAttribute("name", v2.getName());
					lp.addAttribute("id", v2.getLinkpointid().toString());
					lp.addAttribute("period", v2.getPeriod());
					if (pvm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "out");
					}
					if (rm.selectByportid(v2.getLinkpointid()) != null) {
						lp.addAttribute("direction", "in");
					}
					if (dm.getByFrom(v2.getLinkpointid()) != null) {

						lp.addAttribute("datatype", dm.getByFrom(v2.getLinkpointid()).getDatatype());
					}

				});
				// 暂时没有component的错误
				List<_exception> ecplist = em.selectByComp(compv.getComponentid());
				ecplist.forEach((ecpv) -> {
					Element ecpelement = comp.addElement("exception");
					ecpelement.addAttribute("name", ecpv.getName());

				});
				// 新增对于simulink的支持
				List<_task> tasklist = _tm.selectChild(compv.getComponentid());
				tasklist.forEach((taskv) -> {
					Element tsk = comp.addElement("state");
					tsk.addAttribute("Name", taskv.getName());
					tsk.addAttribute("id", taskv.getTaskid().toString());
					tsk.addAttribute("deadline", taskv.getDeadline());
					tsk.addAttribute("period", taskv.getPeriod());
					tsk.addAttribute("wcet", taskv.getWcet());

					List<_exception> exception1 = em.selectByComp(taskv.getTaskid());
					if (exception1.size() > 0) {
						tsk.addAttribute("faultType", exception1.get(0).getName());
					}
					slkstate slkstat = slksm.selectByTask(taskv.getTaskid());
					if (slkstat != null) {

						tsk.addAttribute("exit", slkstat.getExitinfo());
						tsk.addAttribute("faultState", slkstat.getSlkstatecol());
					}

					List<_task> childtasklist = _tm.selectChild(taskv.getTaskid());
					childtasklist.forEach((v6) -> {
						Element child = tsk.addElement("state");
						child.addAttribute("Name", v6.getName());
						child.addAttribute("id", v6.getTaskid().toString());
						child.addAttribute("deadline", v6.getDeadline());
						child.addAttribute("period", v6.getPeriod());
						child.addAttribute("wcet", v6.getWcet());

						List<_exception> exception2 = em.selectByComp(v6.getTaskid());
						if (exception2.size() > 0) {
							child.addAttribute("faultType", exception2.get(0).getName());
						}

						slkstate slkstat2 = slksm.selectByTask(v6.getTaskid());
						if (slkstat2 != null) {
							child.addAttribute("exit", slkstat2.getExitinfo());
							child.addAttribute("faultState", slkstat2.getSlkstatecol());
						}

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
				// transition绑定component
				List<componenttransition> ct = cttm.selectByComponent(compv.getComponentid());
				ct.forEach((ctv) -> {

					List<transitionstate> tsslist = tssm.selectbytransition(Integer.valueOf(ctv.getTransitionid()));
					tsslist.forEach((vtss) -> {
						// 设置transition
						transition ts = tsm.selectByPrimaryKey(vtss.getTransitionid());
						Element transitionElement = comp.addElement("transition");
						transitionElement.addAttribute("id", ts.getTransitionid().toString());
						// 设置event
						_event event = evtm.selectByPrimaryKey(ts.getTriggerid());
						transitionElement.addAttribute("event", event.getName());

						// 设置 source state
						_task s = _tm.selectByPrimaryKey(vtss.getSourceid());
						transitionElement.addAttribute("source", s.getTaskid().toString());

						// 设置dest state
						_task d = _tm.selectByPrimaryKey(vtss.getOutid());
						transitionElement.addAttribute("dest", d.getTaskid().toString());

					});
				});

			});
			// 设置cchannel
			List<communicationchannel> channellist = cchannelMapper.getAll(modeltype);
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

}

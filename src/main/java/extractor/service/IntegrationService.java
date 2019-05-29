package extractor.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
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
import extractor.DAO.mapper._provideMapper;
import extractor.DAO.mapper._requireMapper;
import extractor.DAO.mapper._stateMapper;
import extractor.DAO.mapper._taskMapper;
import extractor.DAO.mapper.communicationchannelMapper;
import extractor.DAO.mapper.componentMapper;
import extractor.DAO.mapper.linkpointMapper;
import extractor.model._state;
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

	public void GenerateIntegaraton(String filename) {
		String dir = "src/main/resources/INTEGRATIONMODEL/";
		// 可配置
		try {
			Document d2 = DocumentHelper.createDocument();
			// 设置系统名
			Element e_comp = d2.addElement("ownedPublicSection");
			e_comp.addAttribute("name", filename);

			// 设置组件
			List<component> r = cm.selectAll();
			r.forEach((v) -> {
				Element comp = e_comp.addElement("component");
				comp.addAttribute("name", v.getName());
				comp.addAttribute("id", v.getComponentid().toString());
				comp.addAttribute("type", v.getType());

				// TODO in out的表示
				// 设置linkpoint,transition
				List<linkpoint> linpoints = lm.getPortUnderCMP(v.getComponentid());
				linpoints.forEach((v2) -> {
					Element lp = comp.addElement("linkpoint");
					lp.addAttribute("name", v2.getName());
					lp.addAttribute("id", v2.getLinkpointid().toString());
					if(pvm.selectByportid(v2.getLinkpointid())!=null) {
						lp.addAttribute("direction", "out");
					}
					if(rm.selectByportid(v2.getLinkpointid())!=null) {
						lp.addAttribute("direction", "in");
					}
				});
				// 设置state
				List<_state> states = sm.getStateUnderCMP(v.getComponentid());
				states.forEach((v3) -> {
					if (v3 != null) {
						Element lp = comp.addElement("state");
						lp.addAttribute("name", v3.getName());
						lp.addAttribute("id", v3.getStateid().toString());
					}
				});
				// 设置task
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

package extractor.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import extractor.DAO.mapper.DeviceMapper;
import extractor.DAO.mapper._provideMapper;
import extractor.DAO.mapper._requireMapper;
import extractor.DAO.mapper.componentMapper;
import extractor.DAO.mapper.connectionsMapper;
import extractor.DAO.mapper.linkpointMapper;
import extractor.model.Device;
import extractor.model._provide;
import extractor.model._require;
import extractor.model.component;
import extractor.model.connections;
import extractor.model.linkpoint;

//模型与数据库的交互功能
@Service
public class ModelService {
	@Autowired
	private componentMapper camArchMapper;
	@Autowired
	private linkpointMapper portsMapper;
	@Autowired
	private _provideMapper pM;
	@Autowired
	private _requireMapper rm;
	@Autowired
	private connectionsMapper connectionsMapper;
	@Autowired
	private DeviceMapper dMapper;

	private int insert_component(component c) {
		return camArchMapper.insert(c);
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
	private int insert_connection(connections c) {
		return connectionsMapper.insert(c);
	}

	private int insert_device(Device d) {
		return dMapper.insert(d);
	}
	//TODO 完善通过组件名获取下面的端口ID
	public Integer getPortIDByComponentName(String name,String portname) {
		return 0;
	}
	public Integer getCmpNameByID(String name){
		return 0;
	}
	public Integer getCChannelBysd(Integer sid,Integer did) {
		return 0;
	}
	public Integer getEventID(String name) {
		return 0;
	}
	public Integer getStateID(String name) {
		return 0;
	}
//	public static void StorePorts(String targetmodelType, String portname) throws Exception {
//
//		String sql = "insert into ";
//		if (targetmodelType.equals("aadl")) {
//			// 先在数据库中注册组件
//			sql += "structurecomponent (ComponentName) values(?)";
//
//		} else if (targetmodelType.equals("sysml")) {
//			sql += "structurecomponent (ComponentName) values(?)";
//		}
//		Connection connection = DBUtil.getConnection();
//		PreparedStatement preStmt = connection.prepareStatement(sql);
//		preStmt.setString(1, portname);
//		preStmt.execute();
//	}
//
//	public static void StoreUnmatched(String portname, String modelType) throws Exception {
//		String sql = "insert into unmatchedports(name,modeltype) values(?,?)";
//		Connection connection = DBUtil.getConnection();
//		PreparedStatement preStmt = connection.prepareStatement(sql);
//		preStmt.setString(1, portname);
//		preStmt.setString(2, modelType);
//		preStmt.execute();
//
//	}

	// 模型元素与元模型进行匹配
	public void srvmatchmeta(String path, String type) throws Exception {
		ModelResolver.MatchComponents(path, type);
		//ModelResolver.ConnectionResolver(path, type);

		ModelResolver.componentlist.forEach((k, v) -> {
			insert_component(v);
		});
		ModelResolver.portlist.forEach((v) -> {
			insert_ports(v);
		});
		ModelResolver.requirelist.forEach((v)->{
			insert_require(v);
		});
		ModelResolver.providelist.forEach((v)->{
			insert_provide(v);
		});
		
//		ModelResolver.conneclist.forEach((k, v) -> {
//			insert_connection(v);
//		});
		ModelResolver.devicelist.forEach((k, v) -> {
			insert_device(v);
		});
		//必须解析了linkpoint后才能将解析communicationChannel
	}
}

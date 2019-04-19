package extractor.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import extractor.DAO.mapper.PortsMapper;
import extractor.DAO.mapper.componentMapper;
import extractor.DAO.mapper.connectionsMapper;
import extractor.model.Ports;
import extractor.model.component;
import extractor.model.connections;

//模型与数据库的交互功能
@Service
public class ModelService {
	@Autowired
	private componentMapper camArchMapper;
	@Autowired
	private PortsMapper portsMapper;
	@Autowired
	private connectionsMapper connectionsMapper;
	
	private int insert_component(component c) {
		return camArchMapper.insert(c);
	}
	private int insert_ports(Ports p) {
		return portsMapper.insert(p);
	}
	private int insert_connection(connections c) {
		return connectionsMapper.insert(c);
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
		ModelResolver.ConnectionResolver(path, type);
		Map<String, component> componentlist = ModelResolver.componentlist;
		Map<String, Ports> portlist = ModelResolver.portlist;
		Map<String, connections> conneclist =ModelResolver.conneclist;
		componentlist.forEach((k, v) -> {
			insert_component(v);
		});
		portlist.forEach((k, v) -> {
				insert_ports(v);
		});
		conneclist.forEach((k, v) -> {
			insert_connection(v);
	});
	}
}

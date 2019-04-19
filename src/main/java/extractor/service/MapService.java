package extractor.service;

//获取各种映射
public class MapService {

	// 获取映射
//	public static Map<Integer, MapElements> GetMapping() throws Exception {
//		Map<Integer, MapElements> reStrings = new HashMap<Integer, MapElements>();
//		String sql = "select * from portmap";
//
//		Connection connection = DBUtil.getConnection();
//		try {
//			PreparedStatement preStmt = connection.prepareStatement(sql);
//			ResultSet rSet = preStmt.executeQuery();
//			int key = 0;
//			String value = null;
//			MapElements mElements = new MapElements();
//			while (rSet.next()) {
//
//				key = rSet.getRow();
//				mElements.setId(rSet.getInt(1));
//				mElements.setSourcemodelType(rSet.getString(2));
//				mElements.setSourcemodelname(rSet.getString(3));
//				mElements.setModeltype_corr(rSet.getString(4));
//				mElements.setModelname_corr(rSet.getString(5));
//				reStrings.put(key, mElements);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return reStrings;
//	}
//
//	public static String GetMappingBySource(String sourcePortName) throws Exception {
//		String sql = "select portname_corr from portmap where sourceportname=?";
//		Connection connection = DBUtil.getConnection();
//		try {
//			PreparedStatement preStmt = connection.prepareStatement(sql);
//			preStmt.setString(1, sourcePortName);
//			ResultSet rSet = preStmt.executeQuery();
//			int key = 0;
//			String value = null;
//			while (rSet.next()) {
//
//				value = rSet.getString(1);
//
//			}
//			return value;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//	public static String GetPortNameInModel(String sourcePortName) throws Exception {
//		String sql = "select portname_corr from portmap where sourceportname=?";
//		Connection connection = DBUtil.getConnection();
//		try {
//			PreparedStatement preStmt = connection.prepareStatement(sql);
//			preStmt.setString(1, sourcePortName);
//			ResultSet rSet = preStmt.executeQuery();
//			int key = 0;
//			String value = null;
//			while (rSet.next()) {
//
//				value = rSet.getString(1);
//			}
//			return value;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
}

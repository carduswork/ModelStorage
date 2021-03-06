package extractor.DAO.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.connections;
@Mapper
@Component
public interface connectionsMapper {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table connections
	 *
	 * @mbg.generated Thu Jun 27 10:12:55 CST 2019
	 */
	int deleteByPrimaryKey(Integer idconnections);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table connections
	 *
	 * @mbg.generated Thu Jun 27 10:12:55 CST 2019
	 */
	int insert(connections record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table connections
	 *
	 * @mbg.generated Thu Jun 27 10:12:55 CST 2019
	 */
	int insertSelective(connections record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table connections
	 *
	 * @mbg.generated Thu Jun 27 10:12:55 CST 2019
	 */
	connections selectByPrimaryKey(Integer idconnections);

	List<connections> selectByfather(Integer fathercmpid);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table connections
	 *
	 * @mbg.generated Thu Jun 27 10:12:55 CST 2019
	 */
	int updateByPrimaryKeySelective(connections record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to
	 * the database table connections
	 *
	 * @mbg.generated Thu Jun 27 10:12:55 CST 2019
	 */
	int updateByPrimaryKey(connections record);
}
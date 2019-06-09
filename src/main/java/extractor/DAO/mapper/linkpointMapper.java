package extractor.DAO.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.linkpoint;

@Mapper
@Component
public interface linkpointMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table linkpoint
     *
     * @mbg.generated Wed May 29 15:04:20 CST 2019
     */
    int deleteByPrimaryKey(Integer linkpointid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table linkpoint
     *
     * @mbg.generated Wed May 29 15:04:20 CST 2019
     */
    int insert(linkpoint record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table linkpoint
     *
     * @mbg.generated Wed May 29 15:04:20 CST 2019
     */
    int insertSelective(linkpoint record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table linkpoint
     *
     * @mbg.generated Wed May 29 15:04:20 CST 2019
     */
    linkpoint selectByPrimaryKey(Integer linkpointid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table linkpoint
     *
     * @mbg.generated Wed May 29 15:04:20 CST 2019
     */
    int updateByPrimaryKeySelective(linkpoint record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table linkpoint
     *
     * @mbg.generated Wed May 29 15:04:20 CST 2019
     */
    int updateByPrimaryKey(linkpoint record);
    
	List<linkpoint> getPortUnderCMP(Integer cmpid);

}
package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.slkstate;
import extractor.model.slkstateWithBLOBs;
@Mapper
@Component
public interface slkstateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    int insert(slkstateWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    int insertSelective(slkstateWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    slkstateWithBLOBs selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    int updateByPrimaryKeySelective(slkstateWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    int updateByPrimaryKeyWithBLOBs(slkstateWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table slkstate
     *
     * @mbg.generated Fri Jul 26 16:10:58 CST 2019
     */
    int updateByPrimaryKey(slkstate record);
    slkstateWithBLOBs selectByTask(Integer taskid);

}
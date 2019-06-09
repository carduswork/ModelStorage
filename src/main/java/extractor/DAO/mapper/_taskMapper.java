package extractor.DAO.mapper;

import java.util.List;

import extractor.model._task;

public interface _taskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Fri Jun 07 09:53:00 CST 2019
     */
    int deleteByPrimaryKey(Integer taskid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Fri Jun 07 09:53:00 CST 2019
     */
    int insert(_task record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Fri Jun 07 09:53:00 CST 2019
     */
    int insertSelective(_task record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Fri Jun 07 09:53:00 CST 2019
     */
    _task selectByPrimaryKey(Integer taskid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Fri Jun 07 09:53:00 CST 2019
     */
    int updateByPrimaryKeySelective(_task record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task
     *
     * @mbg.generated Fri Jun 07 09:53:00 CST 2019
     */
    int updateByPrimaryKey(_task record);
    
    List<_task> selectBypartition(Integer partitionid);

    List<_task> selectChild(Integer fatherid);
}
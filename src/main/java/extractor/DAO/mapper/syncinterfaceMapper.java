package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.syncinterface;
@Mapper
@Component
public interface syncinterfaceMapper {
    int deleteByPrimaryKey(Integer syncinterfaceid);

    int insert(syncinterface record);

    int insertSelective(syncinterface record);

    syncinterface selectByPrimaryKey(Integer syncinterfaceid);

    int updateByPrimaryKeySelective(syncinterface record);

    int updateByPrimaryKey(syncinterface record);
}
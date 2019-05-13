package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model._event;
@Mapper
@Component
public interface _eventMapper {

    int deleteByPrimaryKey(Integer eventid);

    int insert(_event record);

    int insertSelective(_event record);

    _event selectByPrimaryKey(Integer eventid);

    int updateByPrimaryKeySelective(_event record);

    int updateByPrimaryKey(_event record);
    _event getEventID(String eventname);
}
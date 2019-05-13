package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.DispatchChannel;
@Mapper
@Component
public interface DispatchChannelMapper {
    int deleteByPrimaryKey(Integer dispatchchannelid);

    int insert(DispatchChannel record);

    int insertSelective(DispatchChannel record);

    DispatchChannel selectByPrimaryKey(Integer dispatchchannelid);

    int updateByPrimaryKeySelective(DispatchChannel record);

    int updateByPrimaryKey(DispatchChannel record);
}
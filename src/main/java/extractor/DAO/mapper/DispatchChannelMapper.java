package extractor.DAO.mapper;

import extractor.model.DispatchChannel;

public interface DispatchChannelMapper {
    int deleteByPrimaryKey(Integer dispatchchannelid);

    int insert(DispatchChannel record);

    int insertSelective(DispatchChannel record);

    DispatchChannel selectByPrimaryKey(Integer dispatchchannelid);

    int updateByPrimaryKeySelective(DispatchChannel record);

    int updateByPrimaryKey(DispatchChannel record);
}
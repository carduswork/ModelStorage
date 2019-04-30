package extractor.DAO.mapper;

import extractor.model.MessageChannel;

public interface MessageChannelMapper {
    int deleteByPrimaryKey(Integer messagechannelid);

    int insert(MessageChannel record);

    int insertSelective(MessageChannel record);

    MessageChannel selectByPrimaryKey(Integer messagechannelid);

    int updateByPrimaryKeySelective(MessageChannel record);

    int updateByPrimaryKey(MessageChannel record);
}
package extractor.DAO.mapper;

import extractor.model.InvocationChannel;

public interface InvocationChannelMapper {
    int deleteByPrimaryKey(Integer invocationchannelid);

    int insert(InvocationChannel record);

    int insertSelective(InvocationChannel record);

    InvocationChannel selectByPrimaryKey(Integer invocationchannelid);

    int updateByPrimaryKeySelective(InvocationChannel record);

    int updateByPrimaryKey(InvocationChannel record);
}
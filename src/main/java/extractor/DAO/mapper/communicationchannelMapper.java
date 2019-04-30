package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.communicationchannel;
@Mapper
@Component
public interface communicationchannelMapper {
    int deleteByPrimaryKey(Integer communicationchannelid);

    int insert(communicationchannel record);

    int insertSelective(communicationchannel record);

    communicationchannel selectByPrimaryKey(Integer communicationchannelid);

    int updateByPrimaryKeySelective(communicationchannel record);

    int updateByPrimaryKey(communicationchannel record);
}
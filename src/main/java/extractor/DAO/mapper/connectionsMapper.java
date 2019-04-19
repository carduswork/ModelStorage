package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.connections;
@Mapper
@Component
public interface connectionsMapper {
    int deleteByPrimaryKey(Integer idconnections);

    int insert(connections record);

    int insertSelective(connections record);

    connections selectByPrimaryKey(Integer idconnections);

    int updateByPrimaryKeySelective(connections record);

    int updateByPrimaryKey(connections record);
}
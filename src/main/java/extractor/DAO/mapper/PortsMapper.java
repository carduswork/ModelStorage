package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.Ports;
@Mapper
@Component
public interface PortsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Ports record);

    int insertSelective(Ports record);

    Ports selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Ports record);

    int updateByPrimaryKey(Ports record);
}
package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.Device;
@Mapper
@Component
public interface DeviceMapper {
    int deleteByPrimaryKey(Integer deviceid);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(Integer deviceid);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}
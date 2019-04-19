package extractor.DAO.mapper;

import extractor.model.Device;

public interface DeviceMapper {
    int deleteByPrimaryKey(Integer deviceid);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(Integer deviceid);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}
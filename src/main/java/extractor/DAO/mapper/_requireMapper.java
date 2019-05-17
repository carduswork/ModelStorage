package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model._require;
@Mapper
@Component
public interface _requireMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(_require record);

    int insertSelective(_require record);

    _require selectByPrimaryKey(Integer id);
    _require selectByportid(Integer id);
    int updateByPrimaryKeySelective(_require record);

    int updateByPrimaryKey(_require record);
}
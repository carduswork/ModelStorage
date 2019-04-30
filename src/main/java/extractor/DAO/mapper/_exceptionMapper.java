package extractor.DAO.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model._exception;
@Mapper
@Component
public interface _exceptionMapper {
    int deleteByPrimaryKey(Integer exceptionid);

    int insert(_exception record);

    int insertSelective(_exception record);

    _exception selectByPrimaryKey(Integer exceptionid);

    int updateByPrimaryKeySelective(_exception record);

    int updateByPrimaryKey(_exception record);
}
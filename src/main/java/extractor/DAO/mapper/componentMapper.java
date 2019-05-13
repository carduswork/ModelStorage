package extractor.DAO.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.component;
@Mapper
@Component
public interface componentMapper {
	int deleteByPrimaryKey(Integer componentid);

	int insert(component record);

	int insertSelective(component record);

	component selectByPrimaryKey(Integer componentid);

	int updateByPrimaryKeySelective(component record);

	int updateByPrimaryKey(component record);

	Integer getPortIDByComponentName(String cmpname, String portname);

	component getIDbyName(String name);
	
}
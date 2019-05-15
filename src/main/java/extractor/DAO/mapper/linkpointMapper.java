package extractor.DAO.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import extractor.model.linkpoint;

@Mapper
@Component
public interface linkpointMapper {
	int deleteByPrimaryKey(Integer linkpointid);

	int insert(linkpoint record);

	int insertSelective(linkpoint record);

	linkpoint selectByPrimaryKey(Integer linkpointid);

	int updateByPrimaryKeySelective(linkpoint record);

	int updateByPrimaryKey(linkpoint record);

	List<linkpoint> getPortUnderCMP(Integer cmpid);
}
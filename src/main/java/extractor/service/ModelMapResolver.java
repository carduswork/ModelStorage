package extractor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import extractor.DAO.mapper.elementmapMapper;
import extractor.model.elementmap;

@Service("map")
public class ModelMapResolver {
	@Autowired
	elementmapMapper em;
	public void sysmltoaadl(elementmap e) {
		
	}

	public void simulinktoaadl(String source, String dest) {

	}
}

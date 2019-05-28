package extractor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import extractor.DAO.mapper.componentMapper;

//查询
@Service("browseaadl")
public class BrowseAADL {
	@Autowired
	componentMapper pm;

	public void getComponentsByName() {

	}

	public void getComponentByType() {
	}

	public void getlinkpointbyComponentName(String cmpname, String direction) {

	}

	public void getlinkpointbyType() {
	}

	public void getlinkpointbyName() {
	}

	public void getlinkpointbyCommunicationChannel() {
	}

	public void getstateincomponent() {

	}

	public void gettaskincomponent() {
	}

	public void gettransitionintask() {
	}
}

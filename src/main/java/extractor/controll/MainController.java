package extractor.controll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;

import extractor.service.ModelService;
@Controller
@EnableAutoConfiguration
public class MainController {
	@Autowired
	ModelService ms;	
	public void match(String path,String type) throws Exception {
		ms.srvmatchmeta(path,type);
	}
}

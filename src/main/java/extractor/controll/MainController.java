package extractor.controll;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;

import extractor.service.AADLResolver;

@Controller
@EnableAutoConfiguration
public class MainController {
	@Autowired
	AADLResolver ms;
	Map<String, String> aadlFiles = new HashMap<String, String>();

	public void setAadlFiles(Map<String, String> aadlFiles) {
		this.aadlFiles = aadlFiles;
	}

	String markedfolder = "src/main/resources/modelresource/MarkedModelFile/";

	public void restore() throws Exception {
		aadlFiles.forEach((k, v) -> {
			File s = new File(v);
			File d = new File(markedfolder + getName(v));

			try {
				Files.copy(s.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
				aadlFiles.put(k, d.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		});
		
	}

	public void SetSysFileID(String archfilepath, String sysfilepath) throws Exception {
		ms.initComponentID(archfilepath);
		ms.SetSysFileID(archfilepath, sysfilepath);
		ms.setAadlFiles(aadlFiles);
		ms.srvmatchmeta();
		//foreach是倒序的
	}

	private static String getName(String typepath) {
		if (typepath.contains("/")) {

			String[] s = typepath.split("/");
			return s[s.length - 1];
		}
		return "";
	}
}

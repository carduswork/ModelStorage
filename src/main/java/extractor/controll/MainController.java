package extractor.controll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;

import extractor.service.AADLResolver;
import extractor.service.IntegrationService;
import extractor.service.SLKResolver;
import extractor.service.SYSMLResolver;

@Controller
@EnableAutoConfiguration
public class MainController {
	@Autowired
	AADLResolver ar;
	@Autowired
	SYSMLResolver sr;
	@Autowired
	SLKResolver slkr;
	@Autowired
	IntegrationService is;

	Map<String, String> aadlFiles = new HashMap<String, String>();
	Map<String, String> sysmlFiles = new HashMap<String, String>();
	Map<String, String> slkFiles = new HashMap<String, String>();

	public void setAadlFiles(Map<String, String> aadlFiles) {
		this.aadlFiles = aadlFiles;
	}

	public void setSysmlFiles(Map<String, String> sysmlFiles) {
		this.sysmlFiles = sysmlFiles;
	}

	public void setSlkFiles(Map<String, String> slkFiles) {
		this.slkFiles = slkFiles;
	}

	String markedfolder = "src/main/resources/modelresource/MarkedModelFile/";

	public void startIntegration() {
		is.GenerateIntegaraton("aadl.xml","aadl");
		is.GenerateIntegaraton4SYSML("sysml.xml","sysml");
		is.GenerateIntegaraton4SLK("simulink.xml","simulink");
	}
//转存复制到新目录下
	public void restore() throws Exception {
		aadlFiles.forEach((k, v) -> {
			//如果有这个分隔符说明是系统内部结构
			if (v.contains(";")) {
				String[] innersysfile = v.split(";");
				StringBuffer newinnerfile=new StringBuffer();
				for (String v2 : innersysfile) {
					File s = new File(v2);
					File d = new File(markedfolder + getName(v2));

					try {
						Files.copy(s.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
						newinnerfile.append(d.getPath()+";");
						//aadlFiles.put(k, d.getPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				aadlFiles.put(k, newinnerfile.toString());
			} else {
				File s = new File(v);
				File d = new File(markedfolder + getName(v));

				try {
					Files.copy(s.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
					//newinnerfile.append(d.getPath()+";");
					aadlFiles.put(k, d.getPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				//aadlFiles.put(k, newinnerfile.toString());
			}

		});
		sysmlFiles.forEach((k, v) -> {
			File s = new File(v);
			File d = new File(markedfolder + getName(v));

			try {
				Files.copy(s.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
				sysmlFiles.put(k, d.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		slkFiles.forEach((k, v) -> {
			File s = new File(v);
			File d = new File(markedfolder + getName(v));

			try {
				Files.copy(s.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING);
				slkFiles.put(k, d.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

	}

	public void SetSysFileID(String archfilepath, String sysfilepath) throws Exception {
		ar.initComponentID(archfilepath);
		ar.SetSysFileID(archfilepath, sysfilepath);
		ar.setAadlFiles(aadlFiles);

		
	}

	public void begin() throws Exception {
		sr.setSysmlFiles(sysmlFiles);
		slkr.setSlkfile(slkFiles);
		ar.srvmatchmeta();
		sr.srvmatchmeta();
		slkr.srvmatchmeta();
	}

	private static String getName(String typepath) {
		if (typepath.contains("/")) {
			String[] s = typepath.split("/");
			return s[s.length - 1];
		}
		return "";
	}

}

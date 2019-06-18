package extractor;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import extractor.controll.MainController;

@SpringBootApplication
@MapperScan("extractor.DAO.mapper")
public class ConsoleApplication implements CommandLineRunner {
	@Autowired
	MainController mc;

	Map<String, String> aadlFiles = new HashMap<String, String>();
	Map<String, String> sysmlFiles = new HashMap<String, String>();
	Map<String, String> slkFiles = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConsoleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*
		 * aadlFiles.put("系统内部结构",
		 * "src/main/resources/modelresource/JH_FK/packages/kfBefore.aaxl2");
		 * aadlFiles.put("总体架构",
		 * "src/main/resources/modelresource/JH_FK/packages/HardwareArchitecture.aaxl2")
		 * ; aadlFiles.put("错误库",
		 * "src/main/resources/modelresource/JH_FK/packages/ErrorLib.aaxl2");
		 * aadlFiles.put("组件库",
		 * "src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2");
		 * 
		 * sysmlFiles.put("文件",
		 * "src/main/resources/modelresource/JH_FK/packages/dd05.uml");
		 */
		 

		aadlFiles.put("系统内部结构",
				"src/main/resources/modelresource/wkaadl/alarm.aaxl2;src/main/resources/modelresource/wkaadl/operatorinterface.aaxl2;src/main/resources/modelresource/wkaadl/thermostat.aaxl2");
		aadlFiles.put("总体架构", "src/main/resources/modelresource/wkaadl/isolette.aaxl2");
		aadlFiles.put("组件库", "src/main/resources/modelresource/wkaadl/composition.aaxl2");
		aadlFiles.put("错误库", "src/main/resources/modelresource/wkaadl/ErrorLib.aaxl2");
		sysmlFiles.put("文件", "src/main/resources/modelresource/wk.uml");
		slkFiles.put("文件", "src/main/resources/modelresource/abc.xml");
		mc.setAadlFiles(aadlFiles);
		mc.setSysmlFiles(sysmlFiles);
		mc.setSlkFiles(slkFiles);

		mc.restore();

		mc.SetSysFileID("src/main/resources/modelresource/wkaadl/isolette.aaxl2",
				"src/main/resources/modelresource/wkaadl/alarm.aaxl2");
		mc.SetSysFileID("src/main/resources/modelresource/wkaadl/isolette.aaxl2",
				"src/main/resources/modelresource/wkaadl/operatorinterface.aaxl2");
		mc.SetSysFileID("src/main/resources/modelresource/wkaadl/isolette.aaxl2",
				"src/main/resources/modelresource/wkaadl/thermostat.aaxl2");

		mc.SetSysFileID(aadlFiles.get("总体架构"), aadlFiles.get("系统内部结构"));
		mc.begin();
		mc.startIntegration();
	}

}

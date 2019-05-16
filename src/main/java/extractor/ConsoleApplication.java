package extractor;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import extractor.controll.MainController;
import javassist.expr.NewArray;

@SpringBootApplication
@MapperScan("extractor.DAO.mapper")
public class ConsoleApplication implements CommandLineRunner {
	@Autowired
	MainController mc;

	Map<String, String> aadlFiles = new HashMap<String, String>();
	Map<String, String> sysmlFiles = new HashMap<String, String>();

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ConsoleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		aadlFiles.put("系统内部结构","src/main/resources/modelresource/JH_FK/packages/kfBefore.aaxl2");
//		aadlFiles.put("总体架构","src/main/resources/modelresource/JH_FK/packages/HardwareArchitecture.aaxl2");
//		aadlFiles.put("错误库","src/main/resources/modelresource/JH_FK/packages/ErrorLib.aaxl2");
//		aadlFiles.put("组件库", "src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2");
		// aadlFiles.put("错误库","src/main/resources/modelresource/JH_FK/packages/ErrorLib.aaxl2");
		aadlFiles.put("系统内部结构",
				"src/main/resources/modelresource/wkaadl/alarm.aaxl2;src/main/resources/modelresource/wkaadl/operatorinterface.aaxl2;src/main/resources/modelresource/wkaadl/thermostat.aaxl2");
		aadlFiles.put("总体架构", "src/main/resources/modelresource/wkaadl/isolette.aaxl2");
		aadlFiles.put("组件库", "src/main/resources/modelresource/wkaadl/composition.aaxl2");

		sysmlFiles.put("文件", "src/main/resources/modelresource/wk.uml");
		mc.setAadlFiles(aadlFiles);
		mc.setSysmlFiles(sysmlFiles);
		mc.restore();

		mc.SetSysFileID("src/main/resources/modelresource/wkaadl/isolette.aaxl2",
				"src/main/resources/modelresource/wkaadl/alarm.aaxl2");
		mc.SetSysFileID("src/main/resources/modelresource/wkaadl/isolette.aaxl2",
				"src/main/resources/modelresource/wkaadl/operatorinterface.aaxl2");
		mc.SetSysFileID("src/main/resources/modelresource/wkaadl/isolette.aaxl2",
				"src/main/resources/modelresource/wkaadl/thermostat.aaxl2");
		mc.begin();
		mc.startIntegration();
	}

}

package extractor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import extractor.controll.MainController;

@SpringBootApplication
@MapperScan("extractor.DAO.mapper")
public class ConsoleApplication implements CommandLineRunner{
	@Autowired
	MainController ms;
	public static void main(String[] args) throws Exception{
		SpringApplication.run(ConsoleApplication.class, args);		
	}

	@Override
	public void run(String... args) throws Exception {
		//ms.match("src/main/resources/modelresource/JH_FK/packages/kfBefore.aaxl2", "aadl");
		ms.match("src/main/resources/modelresource/JH_FK/packages/Composition.aaxl2", "aadl");
//		ms.match("src/main/resources/modelresource/dd05.uml", "sysml");
//		ms.match("src/main/resources/modelresource/model_xml.xml", "simulink");
	}
}

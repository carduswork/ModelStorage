package extractor.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.json.JSONObject;

import ch.qos.logback.core.joran.conditional.IfAction;

/*处理simulink中的state的文本内容*/
public class MyTextReader {
	private String rawString;
	private String nameString;
	private String jsonString;
	public MyTextReader(String t) {
		rawString=t;
		BufferedReader r=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(t.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));  
		try {
			nameString=r.readLine();
		
		 String line; 
		 StringBuffer strbuf=new StringBuffer();
		while( (line = r.readLine()) != null) {			
			if(!line.trim().equals("")){
		    	 strbuf.append(line);
		     }   
		}
		//冒号后面的部分要加‘
		String[] el=strbuf.toString().split(":|,");
		StringBuffer emptybuffer=new StringBuffer();
		for(int i=0;i<el.length&&!el[i].trim().equals("");i++) {
			if(i%2==0) {				
				emptybuffer.append(el[i]+":");
			}else {
				emptybuffer.append("'"+el[i]+"',");
			}
		}
		//去除末尾的逗号
		String finalattr;
		if(strbuf.length()>0&&emptybuffer.charAt(emptybuffer.length()-1)==',') {
			
			finalattr=emptybuffer.substring(0, emptybuffer.length()-1);
		}else {
			finalattr=emptybuffer.toString();
		}
		jsonString="{"+finalattr+"}";		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String getParam(String p) {
		if(p.equals("Name")) {
			return nameString;
		}else {			
			try {
				JSONObject jsonObj=new JSONObject(jsonString);
				if(jsonObj.has(p)) 
				{				
					String doString = (String) jsonObj.get(p);
					return doString;
				}else
					return "";
				
			}catch(Exception e) 
			{
				System.out.println(jsonString);
				e.printStackTrace();
			}
			return "";
		}
	}
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Input {

	public void read() {
		
		try (BufferedWriter content = new BufferedWriter(new FileWriter("out.txt"));
				BufferedReader reader = new BufferedReader(new FileReader("data/denseS.txt"))) {
			
	           String line;
	           boolean inComment = false;
	           while ((line = reader.readLine()) != null) {
	        	   if(line.isEmpty()){
	        		   continue;
	        	   }
	        	  
	        	  line = line.trim();
	        	   
	        	   

		            if (!inComment) {
		               
		                if (line.startsWith("❄") && line.endsWith("⛏")) {
		                    continue;  
		                } 
		                else if (line.contains("❄") && line.contains("⛏")) {
		                   
		                    line = line.substring(0, line.indexOf("❄")) + line.substring(line.indexOf("⛏"));
		                    
		                }
		                else if(line.contains("❄") && !line.contains("⛏")) {
		                	inComment = true;
		                	continue;  
		                }
		                line = line.replaceAll("⛏", " ");
			            line = line.replaceAll("\\s{2,}", " ");
				       	line = line.replaceAll("\\s", " ");
				       	
				       	line = toLines(line);
		                content.write(line);
		                
		             
		            } else {
		               
		                if (line.endsWith("⛏")) {
		                    inComment = false;
		                    continue;
		                }
		            }
	   
	           }
	           
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
		  
	}
 
	 public String toLines(String line) {
		 StringBuilder result = new StringBuilder();
		 
		 		line.replaceAll("\n","");
		 		

	        	String[] oneline = line.split(" ");
	        	
	        	for(String onechar : oneline) {
	        		if(onechar.isEmpty()){
		        		   continue;
		        	   }
	        		result.append(onechar).append("\n");
	        		
	        	}
	        	
	        
	        
	        return result.toString();
	 }

}

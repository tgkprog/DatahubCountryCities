package s2n.data.cities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

//k94S_k4DGAbPv
public class Import1 {
	
	public static void main(String []args) {
		File f = null;
		if(args.length > 0) {
			f = new File(args[0]);
		}else {
			f = new File("/d/syn/blacksmith/data/cities/2/GEODATASOURCE-CITIES-FREE.TXT");
			//f = new File("/d/syn/blacksmith/data/cities/1/data/world-cities_csv.csv");
		}
		Charset encoding = Charset.defaultCharset();
		try (InputStream in = new FileInputStream(f);
	             Reader reader = new InputStreamReader(in, encoding);
	             // buffer for efficiency
	             Reader buffer = new BufferedReader(reader)) {
				long skip = 9000;
	            long cnt = 0;
	            long max = 6000;
	            
	            skip = 0;
	            cnt = 0;
	            max = 6000;
	            int r = 0;
	            if(skip > 0) {
	            	while((r = reader.read()) != -1 && skip-- > 0) {
	            		
	            	}
	            }
	            while((r = reader.read()) != -1 && cnt++ < max) {
	            	 char ch = (char) r;
	                 System.out.print(ch);
	            }
	            
	       
	            System.out.println("\n\n Read " + cnt + ", is at end? " + (r == -1));
	        }catch(Exception e) {
	        	System.out.println("\n\n err " + e);
	        	e.printStackTrace();
	        }
	}

}

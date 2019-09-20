package s2n.data.json;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import s2n.data.countryCity.merge.Country;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
public class JsonToCsv {
	Gson gson = new Gson();

	String where = "init";
	public static void main(String[] args) {
		new JsonToCsv(args);
	}

	public JsonToCsv(String[] args) {
		Map<String, Object> countriesByIso = new HashMap<>();
		
		try {
			File fctry = new File("/d/syn/blacksmith/data/EntityDetails.json_extended");
			where = "loadCntrys";
			loadJson(fctry);
			
			
			
			writeCsv();
		} catch (Exception e) {
			System.err.println("Err " + where + " " + e);
			e.printStackTrace();
		}
	}

	private void writeCsv() {
		// TODO Auto-generated method stub
		
	}

	private void loadJson(File fctry) {
		// TODO Auto-generated method stub
		
	}
	

}

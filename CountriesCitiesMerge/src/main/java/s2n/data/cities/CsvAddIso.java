package s2n.data.cities;

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

/**
 * Load and map countries by ISO. map cities by name from list 1 map cities by
 * name from list 2 over write, create fake IATA if not present using 2-4 chars
 * of name and pad with Xs to get 4 chars Write cities Mongo bjson file, ready
 * to import.
 */
public class CsvAddIso {
	private static final char DEFAULT_SEPARATOR = ',';
	private static final char COMMA = ',';
	private static final char TAB = '\t';
	private static final char DEFAULT_QUOTE = '"';
	Gson gson = new Gson();

	String where = "init";

	Map<String, Country> countriesByIso;
	Map<String, Country> countriesByName;//<String, Country> 
	Map<String, City> citiesByCode;//

	public static void main(String[] args) {
		new CsvAddIso();
	}

	public CsvAddIso() {
		countriesByIso = new HashMap<>();
		citiesByCode = new HashMap<>();
		countriesByName = new HashMap<>();
		try {
			File fctry = new File("/d/syn/blacksmith/data/cities/1/data2/countries/data_csv2.csv");
			where = "loadCntrys";
			loadCntrys(fctry);
			File fcty = new File("/d/syn/blacksmith/data/cities/1/data/world-cities.csv");
			where = "loadCities 1";
			loadCities(fcty);
			String g = null;
			g = "db.Countries.insert(" ;
			String end = ")";
			writeJson("/d/syn/blacksmith/data/cities/5/world-countries_insert.json_cmds", countriesByName, g, end);
			g = "db.Cities.insert(" ;
			writeJson("/d/syn/blacksmith/data/cities/5/world-cities_insert.json_cmds", citiesByCode, g, end);
			
			g = "db.Cities.remove(" ;
			writeJson("/d/syn/blacksmith/data/cities/5/world-cities-remove2.json_cmds", citiesByCode, g, end);
			
			
			writeJsonCitiesRemove();
		} catch (Exception e) {
			System.err.println("Err " + where + " " + e);
			e.printStackTrace();
		}
	}
	
	private void writeJson(String file, Map map, String g, String e) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		map.forEach((k,v)->{
			//System.out.println("Item : " + k + " c : " + v);
			String jsonInString = gson.toJson(v);
			writer.println(g + jsonInString + e);
			
		});	
	
		writer.close();
		
	}

	private void writeJsonCitiesRemove() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("/d/syn/blacksmith/data/cities/5/world-cities_removeByCountryCode.json_cmds", "UTF-8");
		countriesByName.forEach((k,v)->{			
			writer.println("db.Cities.remove({\"CountryISO\" : \"" + v.ISO + "\"})");			
		});	
	
		writer.close();
		
	}

	private void loadCities(File fcty) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fcty.getPath()));
		long cnt = 0;
		while (scanner.hasNext()) {
			String line = "";
			try {
				line = scanner.nextLine();
				List<String> fl = parseLine(line, ',', DEFAULT_QUOTE);

				System.out.println("City [name " + fl.get(0) + ", countryName= " + fl.get(1) + ".");
				City c = new City();
				// name,country,subcountry,geonameid
				c.Name = fl.get(0);
				c.countryName = fl.get(1);
				c.subCountryStateName = fl.get(2);
				c.geonameid = fl.get(3);

				c.countryISO = getCountryIso(c);
				c.IATACode = c.geonameid;
		
				c._id = getCityCodeFromNameAndCountryIso(c);
				citiesByCode.put(c._id, c);
				cnt++;
			} catch (Exception e) {
				System.out.println("Error parsing city line :" + line + "\n" + e);
				e.printStackTrace();
			}
		}
		System.out.println("\\n****\\n\\nLoaded " + cnt + " cities.\n****\n\n");
		scanner.close();

	}

	private String getCountryIso(City c) {
		String s = null;
		Country cn = countriesByName.get(c.countryName);
		if (cn != null) {
			s = countriesByName.get(c.countryName).ISO;
			return s;
		}
		s = "un_known_" + c.countryName;
		System.out.println("unknown country ISO for " + c);
		return s;
	}

	private String getCityCodeFromNameAndCountryIso(City c) {
		String n = c.geonameid;
		if (n == null) {
			n = c.countryISO + "-" + c.subCountryStateName + "-" + c.Name;
		}		
		return  n;
	}

	private String getCityNameCode(City c) {

		return c.countryISO + "-" + clean(c.Name);
	}

	private String clean(String v) {
		String s = v + "";
		s = s.replace(' ', '_');
		s = s.replace('\'', '-');
		s = s.replace('\"', '-');
		return s;
	}

	private void loadCntrys(File fctry) throws FileNotFoundException {

		Scanner scanner = new Scanner(new File(fctry.getPath()));
		int cnt = 0;
		while (scanner.hasNext()) {
			List<String> fields = parseLine(scanner.nextLine(), DEFAULT_SEPARATOR, DEFAULT_QUOTE);
			// System.out.println("Country [name " + fields.get(0) + ", iso= " +
			// fields.get(1) + " , name=" + fields.get(2) + "]");
			System.out.println("Country [name " + fields.get(0) + ", iso= " + fields.get(1) + ".");
			Country c = new Country();
			c._id = fields.get(1);
			c.Name = fields.get(0);
			c.ISO = c._id;
			countriesByIso.put(c._id, c);
			countriesByName.put(c.Name, c);
			cnt++;
		}
		System.out.println("\\n****\\n\\nLoaded " + cnt + " countries.\n****\n\n");
		scanner.close();

	}

	public List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		// if empty, return!
		if (cvsLine == null && cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				} else if (ch == '\r') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}
}

package s2n.data.countryCity.merge;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import s2n.data.countryCity.merge.write.CountryWithCities;

/**
 * Load and map countries by ISO. map cities by name from list 1 map cities by
 * name from list 2 over write, create fake IATA if not present using 2-4 chars
 * of name and pad with Xs to get 4 chars Write cities Mongo bjson file, ready
 * to import.
 */
public class Merge {
	File dataHubRootFolder = new File("/u/apps/datahub_io");
	File outputRootFolder = new File("/d/syn/blacksmith/data/cities/9");
	Type countryType = new TypeToken<ArrayList<Country>>() {
	}.getType();
	
	Type cityType = new TypeToken<ArrayList<City>>() {
	}.getType();

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char COMMA = ',';
	private static final char TAB = '\t';
	private static final char DEFAULT_QUOTE = '"';
	Gson gson = new Gson();

	String where = "init";

	Map<String, Country> countriesByIso;
	Map<String, Country> countriesByName;// <String, Country>
	Map<String, City> citiesByCode;//

	public static void main(String[] args) {
		new Merge();
	}

	public Merge() {
		countriesByIso = new HashMap<>();
		citiesByCode = new HashMap<>();
		countriesByName = new HashMap<>();
		List<CountryWithCities> countriesWithCities = new ArrayList<>();
		Map<String, CountryWithCities> countriesWithCitiesMap = new HashMap<>();

		File cntryFile = new File(dataHubRootFolder, "core/country-list/data/data_json.json");
		File cityFile = new File(dataHubRootFolder, "core/world-cities/data/world-cities_json.json");
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(cntryFile));
				BufferedInputStream ctybis = new BufferedInputStream(new FileInputStream(cityFile))
				) {
			JsonReader reader = new JsonReader(new InputStreamReader(bis, "UTF-8"));
			JsonReader readerCty = new JsonReader(new InputStreamReader(ctybis, "UTF-8"));
			List<Country> countries = new Gson().fromJson(reader, countryType);
			List<City> cities = new Gson().fromJson(readerCty, cityType);
			String s = gson.toJson(countries);
			System.out.println(s);
			System.out.println("cities " + cities.size());
			countries.forEach(c -> {
				CountryWithCities ct = new CountryWithCities(c);
				countriesWithCitiesMap.put(c.name, ct);
				});
			 
			CountryWithCities orhCntry = new CountryWithCities("o1", "Orphan-cities");
			countriesWithCitiesMap.put(orhCntry.name, orhCntry);
			
			cities.forEach(cty -> {
				CountryWithCities ctw = countriesWithCitiesMap.get(cty.country);
				if(ctw == null) {
					System.err.println("Orphan city " + cty);
					ctw = orhCntry;
				}
				s2n.data.countryCity.merge.write.City cty2 = new s2n.data.countryCity.merge.write.City(cty);
				ctw.cities.add(cty2);
			});
			String s2 = gson.toJson(countriesWithCitiesMap.values());
			File fileOut = new File(outputRootFolder, "o1.jsons");
			Writer out = new BufferedWriter(new FileWriter(fileOut));
			out.write(s2);
			
			out.close();
//			where = "loadCntrys";
//			loadCntrys(fctry);
//			File fcty = new File("/d/syn/blacksmith/data/cities/1/data/world-cities.csv");
//			where = "loadCities 1";
//			loadCities(fcty);
//			String g = null;
//			g = "db.Countries.insert(" ;
//			String end = ")";
//			writeJson("/d/syn/blacksmith/data/cities/8/world-countries_insert.json_cmds", countriesByName, g, end);
//			g = "db.Cities.insert(" ;
//			writeJson("/d/syn/blacksmith/data/cities/8/world-cities_insert.json_cmds", citiesByCode, g, end);
//			
//			g = "db.Cities.remove(" ;
//			writeJson("/d/syn/blacksmith/data/cities/8/world-cities-remove2.json_cmds", citiesByCode, g, end);
//			
//			
//			writeJsonCitiesRemove();
//			System.out.println("\\n****\\n\\nGot " + countryCnt + " countries.\n****\n\n");
		} catch (Exception e) {
			System.err.println("Err " + where + " " + e);
			e.printStackTrace();
		}
	}
}

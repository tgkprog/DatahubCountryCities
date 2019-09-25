package s2n.data.countryCity.merge;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import s2n.data.Aname;
import s2n.data.countryCity.merge.write.CountryWithCities;

/**
 * Load and map countries by ISO. map cities by name from list 1 map cities by name from list 2 over write, create fake IATA if not present
 * using 2-4 chars of name and pad with Xs to get 4 chars Write cities Mongo bjson file, ready to import.
 */
public class Merge {
	private File dataHubRootFolder = new File("/d/syn/blacksmith/data/datahub_io/d1");
	private File outputRootFolder = new File("/d/syn/blacksmith/data/cities/10");
	private Type countryType = new TypeToken<ArrayList<Country>>() {
	}.getType();

	Type cityType = new TypeToken<ArrayList<City>>() {
	}.getType();

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char COMMA = ',';
	private static final char TAB = '\t';
	private static final char DEFAULT_QUOTE = '"';
	private static Merge me;
	private Gson gson = new Gson();

	private String where = "init";

	Map<String, Country> countriesByIso;
	Map<String, Country> countriesByName;// <String, Country>
	Map<String, City> citiesByCode;//

	private int orphanCitiesCoutnryIndex = 0;

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
				BufferedInputStream ctybis = new BufferedInputStream(new FileInputStream(cityFile))) {
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

			File fileOutOrphans = new File(outputRootFolder, "orphanCities.csv");
			final Writer outOrphans = new BufferedWriter(new FileWriter(fileOutOrphans));
			outOrphans.write("citty-geonameid|city-name|country|subcountry\n");
			CountryWithCities orhCntry = new CountryWithCities("o1", "Orphan-cities");
			countriesWithCitiesMap.put(orhCntry.name, orhCntry);
			orphanCitiesCoutnryIndex = 0;
			cities.forEach(cty -> {
				CountryWithCities ctw = countriesWithCitiesMap.get(cty.country);
				if (ctw == null) {
					ctw = processOrpghanCity(countriesWithCitiesMap, cty, outOrphans);
				}
				s2n.data.countryCity.merge.write.City cty2 = new s2n.data.countryCity.merge.write.City(cty);
				ctw.cities.add(cty2);
			});
			Collection<CountryWithCities> countriesList = countriesWithCitiesMap.values();
			File fileOutRem = new File(outputRootFolder, "removeCountries.jsons");
			final Writer outRemovCount = new BufferedWriter(new FileWriter(fileOutRem));
			File fileOut = new File(outputRootFolder, "cc.jsons");
			final Writer out = new BufferedWriter(new FileWriter(fileOut));

			outRemovCount.write("use dev3globalcosmos;\n");
			final Aname nam = new Aname();
			countriesList.forEach(c -> {
				String s2 = gson.toJson(c);

				try {
					// out.write("db.CC.insert(");
					out.write(s2);
					// out.write(")");
					out.write("\n\n");
					nam.name = c.name;
					String s3 = gson.toJson(nam);
					outRemovCount.write("db.c3.remove(" + s3 + ");\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

			});
			// String s2 = gson.toJson(countriesWithCitiesMap.values());
			String info = "\nGot " + countriesList.size() + " countries, countries added due to orphan cities : " + orphanCitiesCoutnryIndex
					+ " countries.\n****\n\n" + " data hub root folder  :" + dataHubRootFolder + "; output root folder "
					+ this.outputRootFolder;
			outRemovCount.write("print (\"Cities size:\")\ndb.c3.count();\n" + info);
			// out.write(s2);
			outRemovCount.close();
			out.close();
			outOrphans.close();
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
			System.out.println("\\n****\\n\\nGot " + countriesList.size() + " countries, orphans : " + orphanCitiesCoutnryIndex
					+ " countries.\n****\n\n" + " data hub root folder  :" + dataHubRootFolder + "; output root folder "
					+ this.outputRootFolder);
		} catch (Exception e) {
			System.err.println("Err " + where + " " + e);
			e.printStackTrace();
		}
	}

	CountryWithCities processOrpghanCity(Map<String, CountryWithCities> countriesWithCitiesMap, City cty, Writer outOrphans) {
		System.err.println("Orphan city " + cty);
		CountryWithCities ctw = null;

		final String s4 = cty.geonameid + "|" + cty.name + "|" + cty.country + "|" + cty.subcountry + "\n";
		try {
			outOrphans.write(s4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orphanCitiesCoutnryIndex++;
		ctw = new CountryWithCities("orp" + orphanCitiesCoutnryIndex, cty.country);
		countriesWithCitiesMap.put(ctw.name, ctw);
		return ctw;

	}
}

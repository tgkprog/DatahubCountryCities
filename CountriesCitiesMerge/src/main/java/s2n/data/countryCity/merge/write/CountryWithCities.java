package s2n.data.countryCity.merge.write;

import java.util.ArrayList;
import java.util.List;


public class CountryWithCities {
	public String _id;
	 
	public String name;
	public List<City> cities = new ArrayList<>();//cities = new ArrayList<>();

	public CountryWithCities() {

	}

	public CountryWithCities(s2n.data.countryCity.merge.Country c) {
		this._id = c._id;
		this.name = c.name;
		
	}

	public CountryWithCities(String code, String name) {
		this._id = code;
		this.name = name;
	}
	
	
	
	
}

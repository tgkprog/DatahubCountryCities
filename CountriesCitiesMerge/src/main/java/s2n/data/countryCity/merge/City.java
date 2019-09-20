package s2n.data.countryCity.merge;

public class City {
	//"country": "Zimbabwe", "geonameid": 1106542, "name": "Chitungwiza", "subcountry": "Harare"
	public String _id;
	public String country;
	public String subcountry;


	public String geonameid;
	public String name;
	@Override
	public String toString() {
		return "City [_id=" + _id + ", countryName=" + country + ", subCountryStateName=" + subcountry
				+ ", geonameid=" + geonameid + ", name=" + name + "]";
	}
	

	
	
}

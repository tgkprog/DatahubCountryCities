package s2n.data.cities;

public class City {
	String _id;
	String countryISO;
	String countryName;
	String subCountryStateName;
	String IATACode;
	String secondaryIataCode;//list?
	String geonameid;
	String Name;
	
	@Override
	public String toString() {
		return "City [_id=" + _id + ", countryIso=" + countryISO + ", countryName=" + countryName
				+ ", subCountryStateName=" + subCountryStateName + ", primaryIataCode=" + IATACode
				+ ", secondaryIataCode=" + secondaryIataCode + ", geonameid=" + geonameid + ", name=" + Name + "]";
	}
	
	
}

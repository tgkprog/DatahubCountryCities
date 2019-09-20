package s2n.data.countryCity.merge.write;

public class City {
	public String _id;
	public String name;
	
	public City(s2n.data.countryCity.merge.City c) {
		this._id = c.geonameid;
		name= c.name;
		
	}
}

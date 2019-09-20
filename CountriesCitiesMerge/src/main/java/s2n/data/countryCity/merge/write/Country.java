package s2n.data.countryCity.merge.write;

 

public class Country {
	public String _id;
 
	public String name;
	
	public Country() {
		
	}
	
	public Country(String code, String name) {
		super();
		this._id = code;
		this.name = name;
	}
	
	
}

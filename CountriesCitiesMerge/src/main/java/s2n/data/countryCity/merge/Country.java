package s2n.data.countryCity.merge;

import com.google.gson.annotations.*;

public class Country {
	@SerializedName("Code")
	public String _id;
	@SerializedName("Name")
	public String name;
	
	public Country() {
		
	}
	
	public Country(String code, String name) {
		super();
		this._id = code;
		this.name = name;
	}
	
	

}

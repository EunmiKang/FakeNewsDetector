package team.proreplyer;

import org.json.simple.JSONArray;

public class Result {
	public JSONArray relatedData_array = new JSONArray();
	public String result = "";
	
	public Result() {
		
	}
	
	public Result(JSONArray relatedData_array, String result) {
		this.relatedData_array = relatedData_array;
		this.result = result;
	}
}

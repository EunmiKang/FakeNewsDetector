package team.proreplyer;

import org.json.simple.JSONArray;

public class Result {
	public JSONArray relatedData_array = new JSONArray();
	public int result_flag = 4;
	public String result;
	public String[] result_string = {"가짜뉴스가 아닙니다!", "가짜뉴스가 아닐 확률이 높습니다!", "가짜뉴스입니다!", "가짜뉴스일 확률이 높습니다!", "판단 유보"};
	
	public Result() {
		
	}
	
	public Result(JSONArray relatedData_array, String result) {
		this.relatedData_array = relatedData_array;
		this.result = result;
	}
	
	public void setResultFlag(int result_flag) {
		this.result_flag = result_flag;
	}
}
package team.proreplyer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class NLProcessor {
	
	public JSONArray NLP(String input) {
		String openApiURL = "http://aiopen.etri.re.kr:8000/WiseNLU";
		String accessKey = "[발급받은 Access Key]"; // 발급받은 Access Key
		String analysisCode = "srl"; // 언어 분석 코드
		String text = input; // 분석할 텍스트 데이터
		Gson gson = new Gson();

		Map<String, Object> request = new HashMap<>();
		Map<String, String> argument = new HashMap<>();

		argument.put("analysis_code", analysisCode);
		argument.put("text", text);

		request.put("access_key", accessKey);
		request.put("argument", argument);

		URL url;
		Integer responseCode = null;
		String returnResult = "";
		try {
			url = new URL(openApiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(gson.toJson(request).getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuffer sb = new StringBuffer();
            
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String responBodyJson = sb.toString();
            
            // http 요청 오류 시 처리
            if ( responseCode != 200 ) {
                // 오류 내용 출력
                System.out.println("[error] http 요청 오류");
                return null;
            }
 
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = null;
            try {
				JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
				JSONObject returnObject = (JSONObject) jsonObject.get("return_object");
				jsonArray = (JSONArray) returnObject.get("sentence");
			} catch (ParseException e) {
				e.printStackTrace();
			}
         
            return jsonArray;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

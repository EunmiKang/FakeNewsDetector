package team.proreplyer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBManager {
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/factcheck?characterEncoding=UTF-8&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASS = "";

	private static Connection conn = null;

	public boolean connectDB() {
		try {
			Class.forName(DRIVER);
			System.out.println("[*]	JDBC load success.");

			conn = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("[*]	Database connect success.");
		} catch (Exception e) {
			System.out.println("[*]	Database connect error: \n" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean insertToDB(JSONArray NLP_Sentence, String date) {
		if (connectDB()) {
			try {
				Statement stmt = conn.createStatement();
				String query = "INSERT INTO fact_data  (Sentence, morp, WSD, word, NE, dependency, SRL, Date, Link) VALUES('"
						+ ((JSONObject) NLP_Sentence.get(0)).get("text") + "','"
						+ ((JSONObject) NLP_Sentence.get(0)).get("morp") + "','"
						+ ((JSONObject) NLP_Sentence.get(0)).get("WSD") + "','"
						+ ((JSONObject) NLP_Sentence.get(0)).get("word") + "','"
						+ ((JSONObject) NLP_Sentence.get(0)).get("NE") + "','"
						+ ((JSONObject) NLP_Sentence.get(0)).get("dependency") + "','"
						+ ((JSONObject) NLP_Sentence.get(0)).get("SRL") + "','" + date + "');";
				int rowCount = stmt.executeUpdate(query);
				if (rowCount == 0) {
					System.out.println("insert data fail");
					return false;
				} else {
					System.out.println("insert data success");
					return true;
				}
			} catch (Exception e) {
				System.out.println("[*]	INSERT data fail: \n" + e.getMessage());
			}
			return true;
		}
		return false;
	}

	public ArrayList<SentenceInfo> readRelatedData(String sbj, String vp) throws SQLException {
	      if (connectDB()) {
	         Statement stmt = conn.createStatement();
	         String sql = "SELECT * FROM factcheck.subject, factcheck.fact_info, factcheck.date WHERE fact_info.tmp_no = date.tmp_no AND subject.sbj_no = date.sbj_no AND sbj = '"+sbj +"' AND verb ='"+ vp+"';";
	         
	         ResultSet rs = stmt.executeQuery(sql);

	         System.out.println("\n쿼리문 : " + sql);
	         ArrayList<SentenceInfo> returnData = new ArrayList<>();
	         while (rs.next()) {
	            String rs_sbj = rs.getString("sbj");
	            String rs_tmp = rs.getString("tmp");
	            String rs_obj = rs.getString("obj");
	            String rs_verb = rs.getString("verb");
	            String rs_location = rs.getString("location");
	            String rs_neg = rs.getString("neg");
	            String rs_adv = rs.getString("adv");
	            String rs_cmp = rs.getString("cmp");
	            String rs_sentence = rs.getString("sentence");
	            String rs_link = rs.getString("LINK");
	            returnData.add(new SentenceInfo(rs_sbj, rs_obj, rs_verb, rs_tmp, rs_location, rs_neg, rs_adv, rs_cmp,rs_sentence,rs_link));
	         }
	         
	         rs.close();
	         stmt.close();
	         conn.close();

	         return returnData;
	      }
	      return null;
	   }


	public static void main(String[] args) {
		DBManager dbManager = new DBManager();
		dbManager.connectDB();
		NLProcessor NLPObject = new NLProcessor();
		Discriminator discriminator = new Discriminator();
		//discriminator.judgeTruth(input, sentenceInfo_input, relatedDatas);
		discriminator.convertDate("내년");
		//insertToDB("최근 한달간 한국콜마의 상장주식수 대비 거래량을 비교해보니 일별 매매회전율이 0.65%로 집계됐다.", "test","2018-08-01");
		
	}

}

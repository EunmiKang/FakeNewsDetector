package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;

import team.proreplyer.*;

public class JdbcTest {
	private static Statement stmt;
	
	
	public static void main(String args[]){

		System.out.println();
		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/factcheck?characterEncoding=UTF-8&serverTimezone=UTC", "[아이디]", "[비밀번호]");
			System.out.println("Success : "+conn.toString());
			
			NLProcessor nlp_Object = new NLProcessor();
			
			stmt= conn.createStatement();
			String contents = "문재인 대통령이 '5월 1일 경기장'에서 집단체조를 관람했다.";
			JSONArray NLP_Sentence = nlp_Object.NLP(contents);
			System.out.println(NLP_Sentence);
			
			SentenceInfo sentenceInfo_object = new SentenceInfo().NLPtoSentenceInfo(contents, NLP_Sentence);
			sentenceInfo_object.setLink("https://");
			
			sentenceInfo_object.sbj = sentenceInfo_object.sbj.replace("'", "\\\'");
			sentenceInfo_object.obj = sentenceInfo_object.obj.replace("'", "\\\'");
			sentenceInfo_object.tmp = sentenceInfo_object.tmp.replace("'", "\\\'");
			sentenceInfo_object.location = sentenceInfo_object.location.replace("'", "\\\'");
			sentenceInfo_object.neg = sentenceInfo_object.neg.replace("'", "\\\'");
			sentenceInfo_object.adv = sentenceInfo_object.adv.replace("'", "\\\'");
			sentenceInfo_object.cmp = sentenceInfo_object.cmp.replace("'", "\\\'");
			sentenceInfo_object.verb = sentenceInfo_object.verb.replace("'", "\\\'");
			sentenceInfo_object.sentence = sentenceInfo_object.sentence.replace("'", "\\\'");

			System.out.println("sbj : " + sentenceInfo_object.sbj);
			System.out.println("obj : " + sentenceInfo_object.obj);
			System.out.println("tmp : " + sentenceInfo_object.tmp);
			System.out.println("location : " + sentenceInfo_object.location);
			System.out.println("neg : " + sentenceInfo_object.neg);
			System.out.println("adv : " + sentenceInfo_object.adv);
			System.out.println("cmp : " + sentenceInfo_object.cmp);
			System.out.println("verb : " + sentenceInfo_object.verb);
			System.out.println("sentence : " + sentenceInfo_object.sentence);

			
			
			
			/* DB에 삽입 */
			int sbj_no = 0;
			int tmp_no = 0;
			String sql_select = "SELECT * FROM subject WHERE SBJ ='" + sentenceInfo_object.sbj + "';";
			ResultSet rs = stmt.executeQuery(sql_select);
			
			boolean flag = false;
			if(rs.next()){
				sbj_no = rs.getInt("SBJ_NO");
				flag = true;
				rs.close();
			}
			else{
				rs.close();
				String sql_Subject= "INSERT INTO subject (SBJ) VALUES('" + sentenceInfo_object.sbj + "');";
				int rowCnt = stmt.executeUpdate(sql_Subject);
				if(rowCnt == 0){
					System.out.println("Insert fail");
				}
				else{
					sql_select = "SELECT * FROM subject WHERE SBJ ='" + sentenceInfo_object.sbj + "';";
					rs = stmt.executeQuery(sql_select);
					if(rs.next()){
						System.out.println("-----------");
						sbj_no = rs.getInt("SBJ_NO");
						flag = true;
						rs.close();
					}
				}
				
			}
			
			if (flag) {
				flag = false;
				sql_select = "Select * From date Where TMP ='" + sentenceInfo_object.tmp + "' and SBJ_NO = " + sbj_no + ";";
				rs = stmt.executeQuery(sql_select);
				
				if (rs.next()) {
					tmp_no = rs.getInt("TMP_NO");
					flag = true;
					rs.close();
				} else {
					rs.close();
					String sql_Date = "INSERT INTO date  (TMP, SBJ_NO) VALUES('" + sentenceInfo_object.tmp + "'," + sbj_no + ");";
					int rowCnt = stmt.executeUpdate(sql_Date);
					if (rowCnt == 0) {
						System.out.println("Insert fail");
					} else {
						sql_select = "Select * From date Where TMP ='" + sentenceInfo_object.tmp + "' and SBJ_NO = " + sbj_no + ";";
						rs = stmt.executeQuery(sql_select);
						if (rs.next()) {
							tmp_no = rs.getInt("TMP_NO");
							flag = true;
							rs.close();
						}
					}
				}
			}
			else{
				System.out.println("subject insert fail.");
			}
			if(flag){
				String sql_Info = "INSERT INTO fact_info (TMP_NO,VERB,OBJ,LOCATION,NEG,ADV,CMP,SENTENCE, LINK) VALUES(" + tmp_no +",'"+ sentenceInfo_object.verb+"','"+ sentenceInfo_object.obj + "','"+ sentenceInfo_object.location +"','"+sentenceInfo_object.neg+"','"+sentenceInfo_object.adv+"','"+sentenceInfo_object.cmp+"','"+sentenceInfo_object.sentence+"','"+sentenceInfo_object.link+"');";
				System.out.println(sql_Info);
				int rowCnt = stmt.executeUpdate(sql_Info);
				if(rowCnt == 0){
					System.out.println("info insert fail.");
				}
				else{
					System.out.println("info insert success.");
				}
			}
			else{// fail
				System.out.println("date insert fail.");
			}
		
			conn.close();
		}catch(SQLException ex) {
			System.out.println("SQLException" + ex);
		}
		catch(Exception ex) {
			System.out.println("Exception:" + ex);
		}
	}
}
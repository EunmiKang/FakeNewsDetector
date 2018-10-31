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
			String contents = "문 대통령이 '5월 1일 경기장'에서 집단체조를 관람했다." ;
			//String contents = "지구촌 최대 겨울 스포츠 축제인 2018 평창 동계올림픽대회 및 동계패럴림픽대회가 열리는 무술년 새해가 밝았다. 조직위원회는 1일 “평창올림픽이 개최되는 새해를 맞아 서울 보신각을 비롯한 전국 7곳에서 진행되는 제야의 종 타종과 해맞이 행사에 참여해 성공적인 대회 개최에 대한 국민적 염원과 성원을 이끌어 내기 위한 캠페인 등을 진행했다”고 밝혔다. 타종 행사는 1일 0시에 서울 보신각과 경기 수원 화성행궁, 대구 국채보상운동기념공원 등 3곳에서 진행됐다. 해맞이 행사는 경북 포항 호미곶(1일 05시30분)과 강원 강릉 경포대(1일 06시), 부산 해군 독도함(1일 05시 30분), 충남 당진 왜목마을(1일 07시) 등 4곳에서 열린다. 서울 보신각에서는 지난 20일부터 5일간 평창 공식 포스트에서 진행된 ‘수호랑·반다비가 되어주세요!’ 특별 이벤트에 참가한 763명의 지원자 중 2명을 선발, 대회 마스코트 탈인형을 착용하고 타종하는 특별한 기회도 주어졌다. 올림픽 마스코트인 수호랑은 쇼트트랙 국가대표를 꿈꾸는 아들을 응원하는 엄마 채혜정씨가, 패럴림픽 마스코트 반다비는 특수학교 교사로서 장애 학생들에게 힘찬 에너지를 주고 싶다는 이애란 씨가 착용했다. 경기 수원 화성행궁 광장에서 진행된 ‘소원이 이루어지는 2017 송년 제야행사’에서는 수호랑·반다비가 타종에 참가했다. 평창 홍보부스에서는 ‘소원 박람회’와 ‘새해 소망 엽서 쓰기’ 이벤트를 통해 시민들이 평창 대회와 함께하는 감동과 희망의 한 해가 되길 기원했다. 구 국채보상운동기념공원에선 제야의 종소리와 함께 2017년 마지막 평창 동계올림픽 성화 불꽃이 타올랐다. 대구에서 타종 행사에 참여한 이희범 조직위원장은 “제야의 종소리로 시작된 2018년 새해, 모든 국민들이 평창 동계올림픽과 패럴림픽의 성공을 응원하고 함께 참여해 주시길 바란다”고 말했다. 경북 포항에서 열린 ‘호미곶 한민족 해맞이 축전’에서는 평창 동계올림픽 성화와 새해 첫 일출 기운을 합치는 이색 퍼포먼스를 진행한다. 오전 7시 33분 해 뜨는 시각에 맞춰 상생의 손 조형물 앞에서 5분간 성화봉과 상생의 손, 해를 일치시켜 새해 기운을 모아 대회 성공 개최를 기원한다. 대회 개최지인 강원도 강릉 경포대에서는 경강선 KTX 개통과 함께 연계된 기차여행으로 찾아온 천여 명의 관광객과 함께 올림픽 오륜기 촛불 밝히기와 올림픽 종목 체험행사를 통해 지구촌 축제의 열정을 함께 공유할 예정이다. 2000여 명의 시민들과 해맞이 이벤트를 진행하는 국내 최대 군함인 해군 독도함 함상에서는 부산 근해로 나가 평창올림픽 앰블럼(ㅍㅊ)을 형상화하고 새해 일출과 함께 풍선을 날리며 대회 성공 염원을 담는다. 평창올림픽 홍보 서포터즈인 ‘화이트 타이거즈’는 일출 명소인 충남 당진 왜목마을을 찾아 시민들과 함께 새해를 맞으며 국민적 관심을 유도할 계획이다. 조직위는 대회 전 마지막 겨울 시즌을 맞아 전국 주요지역에 설치된 홍보존과 홍보체험관을 확대 운영함으로써, 40여일 앞으로 다가온 대회 붐업에 나설 예정이다." ;
			JSONArray NLP_Sentence = nlp_Object.NLP(contents);
			System.out.println(NLP_Sentence);
			
			SentenceInfo sentenceInfo_object = new SentenceInfo().NLPtoSentenceInfo(contents, NLP_Sentence);
			
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
				String sql_Info = "INSERT INTO fact_info  (TMP_NO,VERB,OBJ,LOCATION,NEG,ADV,CMP,SENTENCE, LINK) VALUES(" + tmp_no +",'"+ sentenceInfo_object.verb+"','"+ sentenceInfo_object.obj + "','"+ sentenceInfo_object.location +"','"+sentenceInfo_object.neg+"','"+sentenceInfo_object.adv+"','"+sentenceInfo_object.cmp+"','"+sentenceInfo_object.sentence+"','"+sentenceInfo_object.link+"');";
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
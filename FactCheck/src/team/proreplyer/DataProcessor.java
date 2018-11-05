package team.proreplyer;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONArray;

public class DataProcessor {
	boolean collectFlag = false;

	public static void main(String[] args) {
		DataProcessor dataProcessor = new DataProcessor();
		dataProcessor.processInputData("2018년 9월 19일 오후 2시 30분에 문재인 대통령이 백화점에서 인사를 했습니다.");
	}

	public Result processInputData(String input) {
		/* 사용자가 입력한 문장 자연어처리 */
		JSONArray NLP_input = new NLProcessor().NLP(input);
		System.out.println(NLP_input);
		SentenceInfo sentenceInfo_object = new SentenceInfo().NLPtoSentenceInfo(input, NLP_input);
		if (sentenceInfo_object == null) {
			return new Result(new JSONArray(), "최소한 주어, 동사는 포함되는 문장을 입력해주세요ㅠㅠ");
		} else {
			System.out.println(
					sentenceInfo_object.sbj + " / " + sentenceInfo_object.obj + " / " + sentenceInfo_object.verb);

			ArrayList<SentenceInfo> relatedDatas = getCompareData(sentenceInfo_object.sbj, sentenceInfo_object.verb);

			System.out.println();
			System.out.println("@@@ [" + input + "]에 대한 사실 여부 판단 중....");
			if (relatedDatas.size() == 0) { // 판단유보
				System.out.println("관련 데이터가 없어요.ㅠㅠ 판단 유보!");
				return new Result(new JSONArray(), "판단 유보 : 관련 데이터가 없습니다ㅠㅠ");
			} else {
				Result result = new Discriminator().judgeTruth(input, sentenceInfo_object, relatedDatas);
				return result;
			}
		}
	}

	public ArrayList<SentenceInfo> getCompareData(String sbj, String vp) {
		// 디비 갔다와
		ArrayList<SentenceInfo> compareDatas = new ArrayList<>();
		try {
			new DBManager();
			compareDatas = new DBManager().readRelatedData(sbj, vp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return compareDatas;
	}

}
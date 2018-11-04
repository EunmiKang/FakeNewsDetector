package team.proreplyer;

import java.util.ArrayList;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Discriminator {
	List<String> relatedDatas = new ArrayList<>();

	
	@SuppressWarnings("unchecked")
	public Result judgeTruth(String input, SentenceInfo sentenceInfo_input, ArrayList<SentenceInfo> relatedDatas) {
		int result_flag = 4; // 0:가짜뉴스가 아닙니다, 1:가짜뉴스가 아닐 확률이 높습니다, 2:가짜뉴스입니다, 3:가짜뉴스일 확률이 높습니다, 4:판단유보(관련데이터가 없을때)
		Result result = new Result();
		JSONArray relatedData_array = new JSONArray();
		JSONArray[] result_array = new JSONArray[5];
		ArrayList<SentenceInfo> selectedDatas = new ArrayList<>();
		String[] postpositions_adv = { "에게", "에", "에서", "에게서", "한테", "으로", "로", "와", "과", "보다", "이", "가" }; // 부사격 조사
		String[] postpositions_cmp = { "이", "가" }; // 보격 조사

		/* 부사어, 보어에서 조사 제거 */
		String input_adv = sentenceInfo_input.adv;
		if (!input_adv.equals("null")) {
			for (int i = 0; i < postpositions_adv.length; i++) {
				String input_postposition = input_adv.substring(input_adv.length() - postpositions_adv[i].length());
				if (input_postposition.equals(postpositions_adv[i])) {
					input_adv = input_adv.substring(0, input_adv.length() - postpositions_adv[i].length());
					System.out.println("부사어 : " + input_adv);
					break;
				}
			}
		}
		String input_cmp = sentenceInfo_input.cmp;
		if (!input_cmp.equals("null")) {
			for (int i = 0; i < postpositions_cmp.length; i++) {
				String input_postposition = input_cmp.substring(input_cmp.length() - postpositions_cmp[i].length());
				if (input_postposition.equals(postpositions_cmp[i])) {
					input_cmp = input_cmp.substring(0, input_cmp.length() - postpositions_cmp[i].length());
					System.out.println("보어 : " + input_cmp);
					break;
				}
			}
		}

		convertDate(sentenceInfo_input.tmp);
		System.out.println("지현이가 한 날짜 변환 과연 !\n" + sentenceInfo_input.tmp);

		/* 관련 문장들과 비교 */
		for (int i = 0; i < relatedDatas.size(); i++) {
			int count_diff = 0;
			boolean flag_sameComponent = true, flag_objOrCmp_same = true;
			SentenceInfo cmpData = relatedDatas.get(i);

			if (!sentenceInfo_input.obj.equals("null")) {
				if (!cmpData.obj.equals("null")) { // 목적어 비교
					if (!sentenceInfo_input.obj.equals(cmpData.obj)) { // 다르면
						flag_objOrCmp_same = false;
					}
				} else {
					continue;
				}
			} else if (!sentenceInfo_input.cmp.equals("null")) {
				if (!cmpData.cmp.equals("null")) { // 보어 비교
					if (!sentenceInfo_input.cmp.equals(cmpData.cmp)) { // 다르면
						flag_objOrCmp_same = false;
					}
				} else {
					continue;
				}
			}

			String[] input_advTmpLoc = { sentenceInfo_input.adv, sentenceInfo_input.tmp, sentenceInfo_input.location };
			String[] cmp_advTmpLoc = { cmpData.adv, cmpData.tmp, cmpData.location };
			for (int j = 0; j < 3; j++) {
				if (!input_advTmpLoc[j].equals("null")) {
					if (!cmp_advTmpLoc[j].equals("null")) {
						if (j == 0) {
							if (!cmp_advTmpLoc[j].contains(input_advTmpLoc[j])) {
								count_diff++;
							}
						} else {
							if (!input_advTmpLoc[j].equals(cmp_advTmpLoc[j])) {
								count_diff++;
							}
						}
					} else {
						flag_sameComponent = false;
					}
				} else {
					if (!cmp_advTmpLoc[j].equals("null")) {
						flag_sameComponent = false;
					}
				}
			}
			
			/*
			* 0. 가짜뉴스가 아닙니다: 문장의 성분 구조가 같고 문장 성분들이 모두 같을 경우(neg는 일치)
			* 1. 가짜뉴스가 아닐 확률이 높습니다: 문장의 성분 구조가 다르고 input의 문장 성분들이 모두 맞을 경우(neg는 일치)
			* 2. 가짜뉴스입니다: 문장의 성분 구조가 같고 목적어나 보어가 같고 다른 요소들이 하나만 달라도!(neg 일치) / 문장의 성분 구조가 같고 문장 성분들이 모두 같고 neg가 불일치
			* 3. 가짜뉴스일 확률이 높습니다: 문장의 성분 구조가 다르고 input의 문장 성분 중 하나만 달라도(neg 일치) / 문장의 성분 구조가 다르고 문장 성분들이 모두 같고 neg가 불일치
			* 4. 판단유보: 관련데이터가 없을 때
			 */
			for(int j=0; j<5; j++) {
				result_array[j] = new JSONArray();
			}
			
			if(flag_sameComponent) {	// 문장 성분 구조 같음
				if(flag_objOrCmp_same) {
					if(count_diff == 0) {	// 성분들이 모두 일치
						if (!(cmpData.neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // neg 일치
							if(result_flag > 0) {
								result_flag = 0;
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("sentence", relatedDatas.get(i).sentence);
							jsonObject.put("link", relatedDatas.get(i).link);
							result_array[0].add(jsonObject);
						} else {	// neg 불일치
							if(result_flag > 2) {
								result_flag = 2;
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("sentence", relatedDatas.get(i).sentence);
							jsonObject.put("link", relatedDatas.get(i).link);
							result_array[2].add(jsonObject);
						}
					} else {	// 성분들이 하나라도 다름
						if (!(cmpData.neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // neg 일치
							if(result_flag > 2) {
								result_flag = 2;
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("sentence", relatedDatas.get(i).sentence);
							jsonObject.put("link", relatedDatas.get(i).link);
							result_array[2].add(jsonObject);
						}
					}
				} 
			} else {	// 문장 성분 구조 다름
				if(flag_objOrCmp_same && count_diff == 0) {
					if(count_diff == 0) {	// input의 성분들이 비교 문장과 모두 일치
						if (!(cmpData.neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // neg 일치
							if(result_flag > 1) {
								result_flag = 1;
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("sentence", relatedDatas.get(i).sentence);
							jsonObject.put("link", relatedDatas.get(i).link);
							result_array[1].add(jsonObject);
						} else {	// neg 불일치
							if(result_flag > 3) {
								result_flag = 3;
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("sentence", relatedDatas.get(i).sentence);
							jsonObject.put("link", relatedDatas.get(i).link);
							result_array[3].add(jsonObject);
						}
					} else {	// 성분들이 하나라도 다름
						if (!(cmpData.neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // neg 일치
							if(result_flag > 3) {
								result_flag = 3;
							}
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("sentence", relatedDatas.get(i).sentence);
							jsonObject.put("link", relatedDatas.get(i).link);
							result_array[3].add(jsonObject);
						}
					}
				}
			}
		}

		// 결과 show()
		System.out.println(result.result_string[result_flag]);
		relatedData_array = result_array[result_flag];
		result = new Result(relatedData_array, result.result_string[result_flag]);
		System.out.println("***** 비교에 사용된 데이터 *****");
		for (int i = 0; i < selectedDatas.size(); i++) {
			System.out.println("비교문장" + (i + 1) + ") " + selectedDatas.get(i).sentence);
		}
		return result;
	}

	public String convertDate(String tmp) {
		
		int year, month;
		Calendar cal = Calendar.getInstance ( );
		Date today = new Date();
		Date tomorrow = new Date(today.getTime() + (long) (1000 * 60 * 60 * 24));
		Date yesterday = new Date(today.getTime() - (long) (1000 * 60 * 60 * 24));
		

		SimpleDateFormat formatter01 = new SimpleDateFormat("yyyy년 MM월 dd일");

		if (tmp.equals("오늘")) {
			tmp = formatter01.format(today);
		} else if (tmp.equals("내일")) {
			tmp = formatter01.format(tomorrow);
		} else if (tmp.equals("어제")) {
			tmp = formatter01.format(yesterday);
		} else if (tmp.equals("내년")) {
			year = cal.get ( Calendar.YEAR ) + 1;
			tmp = year+"년";
		} else if (tmp.equals("작년")) {
			year = cal.get ( Calendar.YEAR ) - 1;
			tmp = year+"년";
		}
		System.out.println(tmp);
		return tmp;
	}
}
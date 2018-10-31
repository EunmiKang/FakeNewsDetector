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
		int result_flag = 0; // 0:판단유보, 1:사실, 2:거짓
		Result result = new Result();
		JSONArray relatedData_array = new JSONArray();
		ArrayList<SentenceInfo> selectedDatas = new ArrayList<>();
		String[] postpositions_adv = { "에게", "에", "에서", "에게서", "한테", "으로", "로", "와", "과", "보다", "이", "가" }; // 부사격 조사
		String[] postpositions_cmp = { "이", "가" }; // 보격 조사

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

		/* 관련 문장들과 비교 */
		if (!sentenceInfo_input.obj.equals("null") || !sentenceInfo_input.cmp.equals("null")) { // 목적어 or 보어 있음
			for (int i = 0; i < relatedDatas.size(); i++) {
				if ((!relatedDatas.get(i).obj.equals("null") && !sentenceInfo_input.obj.equals("null"))
						|| (!relatedDatas.get(i).cmp.equals("null") && !input_cmp.equals("null"))) { // 목적어 or 보어 체크
					if (relatedDatas.get(i).obj.equals(sentenceInfo_input.obj)
							|| relatedDatas.get(i).cmp.contains(input_cmp)) { // 목적어 or 보어 같음
						boolean continue_flag = true;
						if (!relatedDatas.get(i).tmp.equals("null") && !sentenceInfo_input.tmp.equals("null")) { // 시간
							// 비교
							if (!relatedDatas.get(i).tmp.equals(sentenceInfo_input.tmp)) { // 시간 다름
								continue_flag = false;
							}
						}
						if (continue_flag && !relatedDatas.get(i).location.equals("null")
								&& !sentenceInfo_input.location.equals("null")) { // 장소 비교
							if (!relatedDatas.get(i).location.contains(sentenceInfo_input.location)) { // 장소 다름
								continue_flag = false;
							}
						}
						if (continue_flag && !relatedDatas.get(i).adv.equals("null") && !input_adv.equals("null")) { // 부사어
																														// 비교
							if (!relatedDatas.get(i).adv.contains(input_adv)) { // 부사어 다름
								continue_flag = false;
							}
						}
						if (continue_flag) { // 나머지 다 똑같!
							if (!(relatedDatas.get(i).neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // true~~~~~
								if (result_flag != 1) {
									selectedDatas.clear();
									relatedData_array.clear();
								}
								result_flag = 1;
								selectedDatas.add(relatedDatas.get(i));
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("sentence", relatedDatas.get(i).sentence);
								jsonObject.put("link", relatedDatas.get(i).link);
								relatedData_array.add(jsonObject);
							} else { // 부정(neg)에 걸림!!
								result_flag = 2;
								selectedDatas.add(relatedDatas.get(i));
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("sentence", relatedDatas.get(i).sentence);
								jsonObject.put("link", relatedDatas.get(i).link);
								relatedData_array.add(jsonObject);
							}
						} else { // 하나라도 다름!
							if (result_flag != 1) {
								result_flag = 2;
								selectedDatas.add(relatedDatas.get(i));
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("sentence", relatedDatas.get(i).sentence);
								jsonObject.put("link", relatedDatas.get(i).link);
								relatedData_array.add(jsonObject);
							}
						}
					} else { // 목적어 or 보어 다름
						if (result_flag != 1) {
							boolean continue_flag = true;
							if (!relatedDatas.get(i).tmp.equals("null") && !sentenceInfo_input.tmp.equals("null")) { // 시간
																														// 비교
								if (!relatedDatas.get(i).tmp.equals(sentenceInfo_input.tmp)) { // 시간 다름
									continue_flag = false;
								}
							}
							if (continue_flag && !relatedDatas.get(i).location.equals("null")
									&& !sentenceInfo_input.location.equals("null")) {
								if (!relatedDatas.get(i).location.equals(sentenceInfo_input.location)) { // 장소 다름
									continue_flag = false;
								}
							}
							if (continue_flag && !relatedDatas.get(i).adv.equals("null")
									&& !sentenceInfo_input.adv.equals("null")) {
								if (!relatedDatas.get(i).adv.equals(sentenceInfo_input.adv)) { // 부사어 다름
									continue_flag = false;
								}
							}
							if (continue_flag) { // 나머지 다 똑같!
								if (!(relatedDatas.get(i).neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // false
									result_flag = 2;
									selectedDatas.add(relatedDatas.get(i));
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("sentence", relatedDatas.get(i).sentence);
									jsonObject.put("link", relatedDatas.get(i).link);
									relatedData_array.add(jsonObject);
								}
							}
						}
					}
				}
			}
		} else if (!input_adv.equals("null")) { // 목적어, 보어 없음 -> 부사어 확인
			for (int i = 0; i < relatedDatas.size(); i++) {
				if (!relatedDatas.get(i).adv.equals("null")) { // 부사어 체크
					if (relatedDatas.get(i).adv.contains(input_adv)) { // 부사어 같음
						boolean continue_flag = true;
						if (!relatedDatas.get(i).tmp.equals("null") && !sentenceInfo_input.tmp.equals("null")) { // 시간
																													// 비교
							if (!relatedDatas.get(i).tmp.equals(sentenceInfo_input.tmp)) { // 시간 다름
								continue_flag = false;
							}
						}
						if (continue_flag && !relatedDatas.get(i).location.equals("null")
								&& !sentenceInfo_input.location.equals("null")) { // 장소 비교
							if (!relatedDatas.get(i).location.equals(sentenceInfo_input.location)) { // 장소 다름
								continue_flag = false;
							}
						}
						if (continue_flag) { // 나머지 다 똑같!
							if (!(relatedDatas.get(i).neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // true~~~~~
								if (result_flag != 1) {
									selectedDatas.clear();
									relatedData_array.clear();
								}
								result_flag = 1;
								selectedDatas.add(relatedDatas.get(i));
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("sentence", relatedDatas.get(i).sentence);
								jsonObject.put("link", relatedDatas.get(i).link);
								relatedData_array.add(jsonObject);
							} else { // 부정(neg)에 걸림!!
								result_flag = 2;
								selectedDatas.add(relatedDatas.get(i));
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("sentence", relatedDatas.get(i).sentence);
								jsonObject.put("link", relatedDatas.get(i).link);
								relatedData_array.add(jsonObject);
							}
						} else { // 하나라도 다름!
							if (result_flag != 1) {
								result_flag = 2;
								selectedDatas.add(relatedDatas.get(i));
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("sentence", relatedDatas.get(i).sentence);
								jsonObject.put("link", relatedDatas.get(i).link);
								relatedData_array.add(jsonObject);
							}
						}
					} else { // 부사어 다름
						if (result_flag != 1) {
							boolean continue_flag = true;
							if (!relatedDatas.get(i).tmp.equals("null") && !sentenceInfo_input.tmp.equals("null")) { // 시간
																														// 비교
								if (!relatedDatas.get(i).tmp.equals(sentenceInfo_input.tmp)) { // 시간 다름
									continue_flag = false;
								}
							}
							if (continue_flag && !relatedDatas.get(i).location.equals("null")
									&& !sentenceInfo_input.location.equals("null")) {
								if (!relatedDatas.get(i).location.equals(sentenceInfo_input.location)) { // 장소 다름
									continue_flag = false;
								}
							}
							if (continue_flag && !relatedDatas.get(i).adv.equals("null")
									&& !sentenceInfo_input.adv.equals("null")) {
								if (!relatedDatas.get(i).adv.equals(sentenceInfo_input.adv)) { // 부사어 다름
									continue_flag = false;
								}
							}
							if (continue_flag) { // 나머지 다 똑같!
								if (!(relatedDatas.get(i).neg.equals("no") ^ sentenceInfo_input.neg.equals("no"))) { // false
									result_flag = 2;
									selectedDatas.add(relatedDatas.get(i));
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("sentence", relatedDatas.get(i).sentence);
									jsonObject.put("link", relatedDatas.get(i).link);
									relatedData_array.add(jsonObject);
								}
							}
						}
					}
				}
			}
		} else { // 목적어, 보어, 부사어 없음!

		}

		// 결과 show(); 해줘야돼
		if (result_flag == 0) {
			System.out.println("판단 유보!");
			result = new Result(relatedData_array, "판단 유보");
		} else {
			if (result_flag == 1) {
				System.out.println("사실!");
				result = new Result(relatedData_array, "사실");
			} else { // result == 2
				System.out.println("가짜뉴스입니다!");
				result = new Result(relatedData_array, "가짜뉴스!");
			}
			System.out.println("***** 비교에 사용된 데이터 *****");
			for (int i = 0; i < selectedDatas.size(); i++) {
				System.out.println("비교문장" + (i + 1) + ") " + selectedDatas.get(i).sentence);
			}
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
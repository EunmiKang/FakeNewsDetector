package team.proreplyer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SentenceInfo {
	public String sbj;
	public String obj;
	public String verb;
	public String tmp;
	public String location;
	public String neg;
	public String adv;
	public String cmp;
	public String sentence;
	public String link = "";
	
	public SentenceInfo() {
		
	}

	public SentenceInfo(String sbj, String obj, String verb, String tmp, String location, String neg, String adv, String cmp, String sentence, String link) {
		this.sbj = sbj;
		this.obj = obj;
		this.verb = verb;
		this.tmp = tmp;
		this.location = location;
		this.neg = neg;
		this.adv = adv;
		this.cmp = cmp;
		this.sentence = sentence;
		this.link = link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public SentenceInfo NLPtoSentenceInfo(String sentence, JSONArray NLP_sentence) {
		JSONObject sentence_obj = (JSONObject) NLP_sentence.get(0);
		JSONArray srlArray = (JSONArray) sentence_obj.get("SRL");
		if (srlArray.size() > 0) {			
			JSONArray dependencyArray = (JSONArray) sentence_obj.get("dependency");
			JSONObject srlObject = (JSONObject) srlArray.get(srlArray.size() - 1);
			String verb = srlObject.get("verb").toString();
			JSONArray argument = (JSONArray) srlObject.get("argument");
			String sbj = "null";
			String obj = "null";
			String tmp = "null";
			String location = "null";
			String neg = "no";
			String adv = "null";
			String cmp = "null";

			for (Object word : argument) {
				JSONObject temp = (JSONObject) word;
				if (temp.get("type").equals("ARG0")) {
					sbj = temp.get("text").toString();
				} else if (temp.get("type").equals("ARG1")) {
					obj = temp.get("text").toString();
				} else if (temp.get("type").equals("ARGM-TMP")) {
					tmp = temp.get("text").toString();
				} else if (temp.get("type").equals("ARGM-LOC")) {
					location = temp.get("text").toString();
				} else if (temp.get("type").equals("ARGM-NEG")) {
					neg = temp.get("text").toString();
				}
			}

			for (Object word : dependencyArray) {
				JSONObject temp = (JSONObject) word;
				if (sbj.equals("null")) {
					if (temp.get("label").toString().contains("SBJ")) { // 주어
						sbj = temp.get("text").toString();
						sbj = sbj.substring(0, sbj.length() - 1); // 뒤에 조사 떼기
						if(obj.contains(sbj)) {
							sbj = obj;
							obj = "null";
						}
					}
				}
				if (temp.get("label").toString().contains("AJT") || temp.get("label").toString().contains("AP")) {// 부사어
					adv = temp.get("text").toString();
				} else if (temp.get("label").toString().contains("CMP")) {// 보어
					cmp = temp.get("text").toString();
				}
			}
			
			if(tmp.equals("null")) {
				JSONArray NEArray = (JSONArray) sentence_obj.get("NE");
				
				for (Object word : NEArray) {
					JSONObject temp = (JSONObject) word;
					if (temp.get("type").toString().contains("DT")) {
						tmp = temp.get("text").toString();
					}
				}
			}

			return new SentenceInfo(sbj, obj, verb, tmp, location, neg, adv, cmp, sentence, link);
		}
		return null;
	}
}
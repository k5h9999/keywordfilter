package com.github.kwfilter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class KeyWordFilter {
	public static final String PATH_DIC_WFC = "/META-INF/dic/wfc.dic";
	public static final String PATH_DIC_FQC = "/META-INF/dic/fqc.dic";
	private HashMap<String, String> wfc_map = null;
	private HashMap<String, String> fqc_map = null;
	private HashMap<String, Integer> firstword_map = new HashMap<String, Integer>();
	private HashMap<String, Integer> firstword_fqc_map = new HashMap<String, Integer>();
	private static KeyWordFilter singleton;

	private KeyWordFilter() {
	}

	public synchronized static KeyWordFilter getInstance() {
		if (singleton == null) {
			singleton = new KeyWordFilter();
		}
		return singleton;
	}

	public synchronized HashMap<String, String> getWfcMap() {
		if (wfc_map == null) {
			wfc_map = getWordMap(PATH_DIC_WFC, "");
		}
		return wfc_map;
	}

	public synchronized HashMap<String, String> getFqcMap() {
		if (fqc_map == null) {
			fqc_map = getWordMap(PATH_DIC_FQC, "");
		}
		return fqc_map;
	}

	public synchronized HashMap<String, Integer> getFirstwordMap() {
		if (firstword_map == null) {
			firstword_map = new HashMap<String, Integer>();
		}
		return firstword_map;
	}

	public synchronized HashMap<String, Integer> getFirstwordFqcMap() {
		if (firstword_fqc_map == null) {
			firstword_fqc_map = new HashMap<String, Integer>();
		}
		return firstword_fqc_map;
	}

	public boolean reload() {
		try {
			wfc_map = null;
			fqc_map = null;
			KeyWordFilter.getInstance().getWfcMap();
			KeyWordFilter.getInstance().getFqcMap();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean restart() {
		try {
			singleton = null;
			getInstance();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private HashMap<String, String> getWordMap(String dicPath, String replace) {
		InputStream is = null;
		HashMap<String, String> wordMap = new HashMap<String, String>();
		try {
			// is = new FileInputStream(dicPath);
			is = KeyWordFilter.class.getResourceAsStream(dicPath);
			if (is == null) {
				System.out.println(dicPath + " not found!!!");
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"), 512);
			String theWord = null;
			HashMap<String, Integer> firstword = getFirstwordMap();
			HashMap<String, Integer> firstword_fqc = getFirstwordFqcMap();

			String ss = null;
			if (dicPath.equals(PATH_DIC_FQC)) {
				do {
					theWord = br.readLine();
					if (theWord != null && !"".equals(theWord.trim())) {
						String[] sss = theWord.trim().toLowerCase().split("@@");
						if (sss[0] != null && !sss[0].equals("")) {
							if (firstword_fqc
									.containsKey(sss[0].toCharArray()[0] + "")
									&& firstword_fqc
											.get(sss[0].toCharArray()[0] + "") > sss[0]
											.length()) {
								// do nothing
							} else {
								firstword_fqc.put(sss[0].toCharArray()[0] + "",
										sss[0].length());
							}
							wordMap.put(sss[0], sss.length == 1 ? "" : sss[1]);
						}
					}
				} while (theWord != null);
			} else {
				do {
					theWord = br.readLine();
					if (theWord != null && !"".equals(theWord.trim())) {
						ss = theWord.trim().toLowerCase();
						if (firstword.containsKey(ss.toCharArray()[0] + "")
								&& firstword.get(ss.toCharArray()[0] + "") > ss
										.length()) {
							// do nothing
						} else {
							firstword.put(theWord.trim().toLowerCase()
									.toCharArray()[0]
									+ "", ss.length());
						}
						wordMap.put(theWord.trim().toLowerCase(), replace);
					}
				} while (theWord != null);

			}

		} catch (IOException ioe) {
			if (is == null) {
				System.err.println(dicPath + " not found!!!");
			} else {
				System.err.println(dicPath + " loading exception.");
			}
			ioe.printStackTrace();

		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return wordMap;
	}

	public String filter_in(String content, String sta, String usid) {
		if (content == null || content.length() <= 0) {
			return "0";
		}
		if (usid == null) {
			usid = "0";
		}
		HashMap<String, String> wfc_map = KeyWordFilter.getInstance()
				.getWfcMap();
		HashMap<String, String> fqc_map = KeyWordFilter.getInstance()
				.getFqcMap();
		HashMap<String, Integer> firstword_map = KeyWordFilter.getInstance()
				.getFirstwordMap();
		HashMap<String, Integer> firstword_fqc_map = KeyWordFilter
				.getInstance().getFirstwordFqcMap();

		int result_type = 0;
		char[] ch = content.toCharArray();
		StringBuffer ret = new StringBuffer();
		StringBuffer word = new StringBuffer();
		int start = 0;
		int step = 0;
		int max_length = 1;
		for (int i = 0; i < ch.length; i++) {
			if (step == 0) {
				start = i;
				step = 1;
			}
			char c = ch[i];
			word.append(c);
			String tmp = word.toString();
			if (i == start) {
				if (!firstword_fqc_map.containsKey(tmp.toLowerCase())) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
					continue;
				} else {
					max_length = firstword_fqc_map.get(tmp.toLowerCase());
				}
			}
			if (fqc_map.containsKey(tmp.toLowerCase())) {
				ret.append(fqc_map.get(tmp.toLowerCase()));
				step = 0;
			} else if (word.length() >= max_length || i >= ch.length - 1) {
				step = 0;
				ret.append(ch[start]);
				word.delete(0, word.length());
				i = start;
			}
		}
		ch = ret.toString().toCharArray();
		word = new StringBuffer();
		start = 0;
		step = 0;
		if (sta.equals("1")) {
			for (int i = 0; i < ch.length; i++) {
				if (step == 0) {
					start = i;
					step = 1;
				}
				char c = ch[i];
				word.append(c);
				String tmp = word.toString();
				if (i == start) {

					if (!firstword_map.containsKey(tmp.toLowerCase())) {
						step = 0;
						word.delete(0, word.length());
						i = start;
						continue;
					} else {
						max_length = firstword_map.get(tmp.toLowerCase());
					}
				}
				if (wfc_map.containsKey(tmp.toLowerCase())) {
					result_type = 2;
					break;
				} else if (word.length() >= max_length || i >= ch.length - 1) {
					step = 0;
					word.delete(0, word.length());
					i = start;
				}
			}
		} else if (sta.equals("0")) {
			for (int i = 0; i < ch.length; i++) {
				if (step == 0) {
					start = i;
					step = 1;
				}
				char c = ch[i];
				word.append(c);
				String tmp = word.toString();
				if (i == start) {
					if (!firstword_map.containsKey(tmp.toLowerCase())) {
						step = 0;
						word.delete(0, word.length());
						i = start;
						continue;
					} else {
						max_length = firstword_map.get(tmp.toLowerCase());
					}
				}
				if (wfc_map.containsKey(tmp.toLowerCase())) {
					result_type = 2;
					break;
				} else if (word.length() >= max_length || i >= ch.length - 1) {
					step = 0;
					word.delete(0, word.length());
					i = start;
				}
			}
		}
		return Integer.toString(result_type);

	}

	public String filter_out(String content, String sta, String usid) {
		if (content == null || content.length() <= 0) {
			return "";
		}
		if (usid == null) {
			usid = "0";
		}
		HashMap<String, String> wfc_map = KeyWordFilter.getInstance()
				.getWfcMap();
		HashMap<String, String> fqc_map = KeyWordFilter.getInstance()
				.getFqcMap();
		HashMap<String, Integer> firstword_map = KeyWordFilter.getInstance()
				.getFirstwordMap();
		HashMap<String, Integer> firstword_fqc_map = KeyWordFilter
				.getInstance().getFirstwordFqcMap();

		char[] ch = content.toCharArray();
		StringBuffer ret = new StringBuffer();
		StringBuffer word = new StringBuffer();
		int start = 0;
		int step = 0;
		int max_length = 1;
		for (int i = 0; i < ch.length; i++) {
			if (step == 0) {
				start = i;
				step = 1;
			}
			char c = ch[i];
			word.append(c);
			String tmp = word.toString();
			if (i == start) {
				if (!firstword_fqc_map.containsKey(tmp.toLowerCase())) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
					continue;
				} else {
					max_length = firstword_fqc_map.get(tmp.toLowerCase());
				}
			}
			if (fqc_map.containsKey(tmp.toLowerCase())) {
				ret.append(fqc_map.get(tmp.toLowerCase()));
				step = 0;
			} else if (word.length() >= max_length || i >= ch.length - 1) {
				step = 0;
				ret.append(ch[start]);
				word.delete(0, word.length());
				i = start;
			}
		}
		ch = ret.toString().toCharArray();
		ret = new StringBuffer();
		word = new StringBuffer();
		start = 0;
		step = 0;

		if (sta.equals("1")) {
			for (int i = 0; i < ch.length; i++) {
				// System.out.println("i="+i+"\tstart="+start+"\tstep="+step+"\tret="+ret.toString());
				if (step == 0) {
					start = i;
					step = 1;
				}
				char c = ch[i];
				word.append(c);
				String tmp = word.toString();
				if (i == start) {

					if (!firstword_map.containsKey(tmp.toLowerCase())) {
						step = 0;
						ret.append(ch[start]);
						word.delete(0, word.length());
						i = start;
						continue;
					} else {
						max_length = firstword_map.get(tmp.toLowerCase());
					}
				}
				if (wfc_map.containsKey(tmp.toLowerCase())) {
					ret.append("");
					step = 0;
				} else if (word.length() >= max_length || i >= ch.length - 1) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
				}

			}
		} else if (sta.equals("0")) {
			for (int i = 0; i < ch.length; i++) {
				if (step == 0) {
					start = i;
					step = 1;
				}
				char c = ch[i];
				word.append(c);
				String tmp = word.toString();
				if (i == start) {
					if (!firstword_map.containsKey(tmp.toLowerCase())) {
						step = 0;
						ret.append(ch[start]);
						word.delete(0, word.length());
						i = start;
						continue;
					} else {
						max_length = firstword_map.get(tmp.toLowerCase());
					}
				}
				if (wfc_map.containsKey(tmp.toLowerCase())) {
					ret.append("");
					step = 0;
				} else if (word.length() >= max_length || i >= ch.length - 1) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
				}
			}
		}

		String rs = ret.toString().replace("⊙", "");
		return rs;
	}

	public String filter_search(String content) {
		if (content == null || content.length() <= 0) {
			return "";
		}
		HashMap<String, String> fqc_map = KeyWordFilter.getInstance()
				.getFqcMap();
		HashMap<String, String> wfc_map = KeyWordFilter.getInstance()
				.getWfcMap();
		HashMap<String, Integer> firstword_map = KeyWordFilter.getInstance()
				.getFirstwordMap();
		HashMap<String, Integer> firstword_fqc_map = KeyWordFilter
				.getInstance().getFirstwordFqcMap();

		char[] ch = content.toCharArray();
		StringBuffer ret = new StringBuffer();
		StringBuffer word = new StringBuffer();
		int start = 0;
		int step = 0;
		int max_length = 1;
		for (int i = 0; i < ch.length; i++) {
			if (step == 0) {
				start = i;
				step = 1;
			}
			char c = ch[i];
			word.append(c);
			String tmp = word.toString();
			if (i == start) {
				if (!firstword_fqc_map.containsKey(tmp.toLowerCase())) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
					continue;
				} else {
					max_length = firstword_fqc_map.get(tmp.toLowerCase());
				}
			}
			if (fqc_map.containsKey(tmp.toLowerCase())) {
				ret.append(fqc_map.get(tmp.toLowerCase()));
				step = 0;
			} else if (word.length() >= max_length || i >= ch.length - 1) {
				step = 0;
				ret.append(ch[start]);
				word.delete(0, word.length());
				i = start;
			}
		}
		ch = ret.toString().toCharArray();
		ret = new StringBuffer();
		word = new StringBuffer();
		start = 0;
		step = 0;

		for (int i = 0; i < ch.length; i++) {
			if (step == 0) {
				start = i;
				step = 1;
			}
			char c = ch[i];
			word.append(c);
			String tmp = word.toString();
			if (i == start) {
				if (!firstword_map.containsKey(tmp.toLowerCase())) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
					continue;
				} else {
					max_length = firstword_map.get(tmp.toLowerCase());
				}
			}
			if (wfc_map.containsKey(tmp.toLowerCase())) {
				ret.append("");
				step = 0;
			} else if (word.length() >= max_length || i >= ch.length - 1) {
				step = 0;
				ret.append(ch[start]);
				word.delete(0, word.length());
				i = start;
			}
		}

		String rs = ret.toString().replace("⊙", "");
		return rs;
	}

	public String filter_jk(String content, String sta, String usid,
			String mgcstyle1, String mgcstyle2, String wfcstyle1,
			String wfcstyle2) {
		if (content == null || content.length() <= 0) {
			return "";
		}
		if (usid == null) {
			usid = "0";
		}
		HashMap<String, String> wfc_map = KeyWordFilter.getInstance()
				.getWfcMap();
		HashMap<String, String> fqc_map = KeyWordFilter.getInstance()
				.getFqcMap();
		HashMap<String, Integer> firstword_map = KeyWordFilter.getInstance()
				.getFirstwordMap();
		HashMap<String, Integer> firstword_fqc_map = KeyWordFilter
				.getInstance().getFirstwordFqcMap();

		char[] ch = content.toCharArray();
		StringBuffer ret = new StringBuffer();
		StringBuffer word = new StringBuffer();
		int start = 0;
		int step = 0;
		int max_length = 1;
		for (int i = 0; i < ch.length; i++) {
			if (step == 0) {
				start = i;
				step = 1;
			}
			char c = ch[i];
			word.append(c);
			String tmp = word.toString();
			if (i == start) {
				if (!firstword_fqc_map.containsKey(tmp.toLowerCase())) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
					continue;
				} else {
					max_length = firstword_fqc_map.get(tmp.toLowerCase());
				}
			}
			if (fqc_map.containsKey(tmp.toLowerCase())) {
				ret.append(fqc_map.get(tmp.toLowerCase()));
				step = 0;
			} else if (word.length() >= max_length || i >= ch.length - 1) {
				step = 0;
				ret.append(ch[start]);
				word.delete(0, word.length());
				i = start;
			}
		}
		ch = ret.toString().toCharArray();
		ret = new StringBuffer();
		word = new StringBuffer();
		start = 0;
		step = 0;

		if (sta.equals("1")) {
			for (int i = 0; i < ch.length; i++) {
				if (step == 0) {
					start = i;
					step = 1;
				}
				char c = ch[i];
				word.append(c);
				String tmp = word.toString();
				if (i == start) {
					if (!firstword_map.containsKey(tmp.toLowerCase())) {
						step = 0;
						ret.append(ch[start]);
						word.delete(0, word.length());
						i = start;
						continue;
					} else {
						max_length = firstword_map.get(tmp.toLowerCase());
					}
				}
				if (wfc_map.containsKey(tmp.toLowerCase())) {
					ret.append(wfcstyle1);
					ret.append(tmp.toLowerCase());
					ret.append(wfcstyle2);
					step = 0;
				} else if (word.length() >= max_length || i >= ch.length - 1) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
				}
			}
		} else if (sta.equals("0")) {
			for (int i = 0; i < ch.length; i++) {
				if (step == 0) {
					start = i;
					step = 1;
				}
				char c = ch[i];
				word.append(c);
				String tmp = word.toString();
				if (i == start) {
					if (!firstword_map.containsKey(tmp.toLowerCase())) {
						step = 0;
						ret.append(ch[start]);
						word.delete(0, word.length());
						i = start;
						continue;
					} else {
						max_length = firstword_map.get(tmp.toLowerCase());
					}
				}
				if (wfc_map.containsKey(tmp.toLowerCase())) {
					ret.append(wfcstyle1);
					ret.append(tmp.toLowerCase());
					ret.append(wfcstyle2);
					step = 0;
				} else if (word.length() >= max_length || i >= ch.length - 1) {
					step = 0;
					ret.append(ch[start]);
					word.delete(0, word.length());
					i = start;
				}
			}
		}

		String rs = ret.toString().replace("⊙", "");
		return rs;
	}

	public static void main(String[] args) {
		String str = "一个网站就像一个人，存在一个从小到大白粉的过程。养一个网站和养一个人一样，不同时期漂白粉需要不同的方法，口交--不同的方法下有共24口交换机同的原则。";

		Long startTime = System.currentTimeMillis();
		WordFilter wf = new WordFilter();
		System.out.println(wf.filter_search(str));
		System.out
				.println(wf
						.filter_jk_info("一个网站就像一个人"));
		Long endTime = System.currentTimeMillis();

		System.out.println("时间：" + (endTime - startTime));
	}
}

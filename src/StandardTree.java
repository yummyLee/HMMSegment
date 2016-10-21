import java.util.ArrayList;
public class StandardTree {

	private TrieNode root = new TrieNode(' ');
	private int maxlen = 4;

	public void insert(String word) {
		char[] str_arr = word.toCharArray();
		TrieNode pNode = root;
		for (int i = 0; i < str_arr.length; i++) {
			if (pNode.children.get(str_arr[i]) == null) {
				TrieNode temp_child = new TrieNode(str_arr[i]);
				pNode.children.put(str_arr[i], temp_child);
			}
			pNode = pNode.children.get(str_arr[i]);
			if (i == str_arr.length - 1) {
				pNode.is_end = true;
			}
		}
	}

	public boolean match(String word) {

		char[] str_arr = word.toCharArray();
		TrieNode pNode = root;
		for (int i = 0; i < str_arr.length; i++) {
			if (pNode.children.get(str_arr[i]) != null) {
				pNode = pNode.children.get(str_arr[i]);
				if (i == str_arr.length - 1 && pNode.is_end) {
					return true;
				}
			}
		}

		return false;
	}

	public String segment(String sentence) {
		sentence += "°£";
		ArrayList<String> res = new ArrayList<>();
		int cur_start = 0;
		String word = "";
		while (true) {
			int m = maxlen, n = sentence.length() - cur_start;
			if (n == 1) {
				break;
			}
			if (n < m) {
				m = n;
			}
			word = sentence.substring(cur_start, cur_start + m);
			while (true) {
				if (match(word)) {
					res.add(word);
//					System.out.println("1°¢∆•≈‰µΩ\"" + word + "\"");
					break;
				} else if (word.length() > 1) {
					word = word.substring(0, word.length() - 1);
				} else {
					res.add(word);
//					System.out.println("2°¢∆•≈‰µΩ\"" + word + "\"");
					break;
				}
			}
			cur_start = cur_start + word.length();
		}
		word = sentence.substring(cur_start, sentence.length());
		res.add(word);
//		System.out.println("3°¢∆•≈‰µΩ\"" + word + "\"");

		String final_result_str = HMMSegment.handleArrayList(res);
		final_result_str = final_result_str.substring(0, final_result_str.length() - 2);
		
		return final_result_str;
	}

}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HandleCorpus {

	public static void handleCorpus() throws IOException {
		String content;
		content = ReadFile.readFileContent(new File("1998-01-2003版-带音.txt"));

		String[] words = content.split("  ");

		File file = new File("handleCorpusResult.txt");
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

//		int max = 0;

		for (String word : words) {

			word = word.substring(0, word.indexOf('/'));
			if (isStrChinese(word)) {
				bufferedWriter.write(word + "  ");
			} else {
				if (isMixed(word)) {
					boolean can_add=false;
					if (word.substring(0, 1).equals("[")) {
						word = word.substring(1);
						can_add=true;
						// System.out.println(word);
					}
					if (word.indexOf("{") > 0) {
						can_add=true;
						word = word.substring(0, word.indexOf("{"));
						// System.out.println(word);
					}
					if (can_add) {
						
						bufferedWriter.write(word + " ");
					}
				}
			}

		}
		System.out.println("HandleCorpus Completed!");
		bufferedWriter.close();
	}

	public static boolean isChinese(char wo) {
		if (wo >= '\u4e00' && wo <= '\u9fa5') {
			return true;
		}
		return false;
	}

	public static boolean isStrChinese(String str) {

		for (int i = 0; i < str.length(); i++) {
			if (!isChinese(str.substring(i, i + 1).toCharArray()[0])) {
				// System.out.println(str.substring(i, i + 1));
				return false;
			}
		}
		return true;
	}

	public static boolean isMixed(String str) {
		boolean is_contain_chinese = false;
		boolean is_contain_puctuation = false;

		for (int i = 0; i < str.length(); i++) {
			if (!isChinese(str.substring(i, i + 1).toCharArray()[0])) {
				is_contain_puctuation = true;
			} else {
				is_contain_chinese = true;
			}
		}
		if (is_contain_chinese && is_contain_puctuation) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		try {
			handleCorpus();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// isStrChinese("济济一堂");
	}
}

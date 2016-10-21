import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Yummy
 * @date : 2016年9月25日 下午1:23:00 I lost something,but I have more freedom now.
 */

public class HMM {

	public static int[] wordsCountByLength(String str) {
		int[] wc = new int[17];

		String[] words = str.split(" ");

		for (String word : words) {
			wc[word.length()]++;
		}
		for (int i : wc) {
			System.out.print(i + " ");
		}
		return wc;
	}

	public static void preGenerateAMatrix() throws IOException {
		String content = "";
		try {
			content = ReadFile.readFileContent(new File("1998-01-2003版-带音.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] sentences = content.split("/w");
		File sentences_segment = new File("sentences_segment2.txt");
		FileWriter fileWriter = new FileWriter(sentences_segment);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (String str : sentences) {
			String[] words = str.split("  ");
			for (String word : words) {

				int index = word.indexOf('/');
				if (index < 0) {
					continue;
				}
				word = word.substring(0, index);
				if (HandleCorpus.isStrChinese(word)) {
					word = word.replace(" ", "");
					bufferedWriter.write(word + "  ");
				} else {
					if (HandleCorpus.isMixed(word)) {
						boolean can_add = false;
						if (word.substring(0, 1).equals("[")) {
							word = word.substring(1);
							can_add = true;
							// System.out.println(word);
						}
						if (word.indexOf("{") > 0) {
							can_add = true;
							word = word.substring(0, word.indexOf("{"));
							// System.out.println(word);
						}
						if (can_add) {
							word = word.replace(" ", "");
							bufferedWriter.write(word + "  ");
						}
					}
				}

			}
			bufferedWriter.write("#");
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}
	
	public static void  preHandle() throws IOException {
		File file=new File("sentences_segment4.txt");
		FileReader fileReader=new FileReader(file);
		BufferedReader bufferedReader=new BufferedReader(fileReader);
		File file2=new File("sentences_segment5.txt");
		FileWriter fileWriter=new FileWriter(file2);
		BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
		String str="";
		while((str=bufferedReader.readLine())!=null){
			bufferedWriter.write(str+"#");
			bufferedWriter.newLine();
		}
		bufferedReader.close();
		bufferedWriter.close();
	}

	public static void generateAMatrixMark() throws IOException {

		String content = ReadFile.readFileContent(new File("sentences_segment2.txt"));
		String[] sentences = content.split("#");
		File sentences_segment = new File("sentences_segment3.txt");
		FileWriter fileWriter = new FileWriter(sentences_segment);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		HashMap<String, Integer> matrix_b_data = new HashMap<>();
		HashMap<String, Integer> matrix_b_sum_data = new HashMap<>();
		for (String sentence : sentences) {
			if (sentence.length() == 0) {
				// System.out.println("0000");
				continue;
			}
			String[] words = sentence.split("  ");
			String sentence_mark = "";
			for (String word : words) {
				// word = word.replace(" ", "");
				String words_mark = generateSBIEMark(word);

				for (int i = 0; i < word.length(); i++) {
					Integer word_count = matrix_b_sum_data.get(word.substring(i, i + 1));
					if (word_count != null) {
						matrix_b_sum_data.put(word.substring(i, i + 1), word_count + 1);
					} else {
						matrix_b_sum_data.put(word.substring(i, i + 1), 1);
					}

					String key = word.substring(i, i + 1) + "|" + words_mark.substring(i, i + 1);
					Integer count = matrix_b_data.get(key);
					if (count != null) {
						matrix_b_data.put(key, count + 1);
					} else {
						matrix_b_data.put(key, 1);
					}
				}
				sentence_mark += words_mark;
			}
			if (sentence_mark.length() > 0) {
				bufferedWriter.write(sentence_mark + "#");
				bufferedWriter.newLine();
			} else {
				System.out.println("000");
			}

		}

		bufferedWriter.close();

		// System.out.println(matrix_b_data);
		// System.out.println(matrix_b_sum_data);
		System.out.println(matrix_b_sum_data.size());
		ReadFile.writeFileContent(new File("matrix_word_count.txt"), matrix_b_sum_data.toString());

		HashMap<String, Double> matrix_b = new HashMap<>();

		File matrix_b_file = new File("matrix_b.txt");
		FileWriter matrix_b_fw = new FileWriter(matrix_b_file);
		BufferedWriter matrix_b_bw = new BufferedWriter(matrix_b_fw);

		File matrix_b2_file = new File("matrix_b2.txt");
		FileWriter matrix_b2_fw = new FileWriter(matrix_b2_file);
		BufferedWriter matrix_b2_bw = new BufferedWriter(matrix_b2_fw);

		for (Entry<String, Integer> e : matrix_b_data.entrySet()) {
			double p = (double) e.getValue() / (double) matrix_b_sum_data.get(e.getKey().substring(0, 1));
			// System.out.println(p);
			matrix_b.put(e.getKey(), p);
			matrix_b_bw.write(e.getKey() + "  " + p + "#");
			matrix_b_bw.newLine();
			matrix_b2_bw.write(e.getKey() + "  " + e.getValue() + "#");
			matrix_b2_bw.newLine();
		}
		matrix_b_bw.close();
		matrix_b2_bw.close();
		// System.out.println(matrix_b);

	}

	public static String generateSBIEMark(String word) {
		String mark = "";
		if (word.length() == 0) {
			return mark;
		} else if (word.length() == 1) {
			mark = "S";
		} else {
			mark = "B";
			for (int i = 1; i < word.length() - 1; i++) {
				mark += "I";
			}
			mark += "E";
		}

		return mark;
	}

	public static void generateAMatrix() throws IOException {

		HashMap<String, Integer> state_count = new HashMap<>();
		double[] start_mark_count = new double[4];
		String state_content = ReadFile.readFileContent(new File("sentences_segment3.txt"));
		String[] sentences_state = state_content.split("#");
		for (String sentence_state : sentences_state) {
			String start_mark = sentence_state.substring(0, 1);
			if (start_mark.equals("B")) {
				start_mark_count[1]++;
			} else if (start_mark.equals("S")) {
				start_mark_count[0]++;
			} else {
				System.out.println("error");
			}
			for (int i = 0; i < sentence_state.length() - 1; i++) {
				String state_str = sentence_state.substring(i, i + 1) + ">" + sentence_state.substring(i + 1, i + 2);
				Integer count = state_count.get(state_str);
				if (count != null) {
					state_count.put(state_str, count + 1);
				} else {
					state_count.put(state_str, 1);
				}
			}
		}

		double[][] matrix_a = new double[4][4];
		double[] mark_count = new double[4];
		for (Entry<String, Integer> e : state_count.entrySet()) {
			switch (e.getKey().substring(0, 1)) {
			case "S":
				mark_count[0] += e.getValue();
				break;
			case "B":
				mark_count[1] += e.getValue();
				break;
			case "I":
				mark_count[2] += e.getValue();
				break;
			case "E":
				mark_count[3] += e.getValue();
				break;
			default:
				break;
			}

		}
//		System.out.println(state_count);
		for (Entry<String, Integer> e : state_count.entrySet()) {
			switch (e.getKey()) {

			case "S>S":
				// System.out.println(e.getValue() + " + " + mark_count[0]);
				matrix_a[0][0] = e.getValue() / mark_count[0];
				break;
			case "S>B":
				matrix_a[0][1] = e.getValue() / mark_count[0];
				break;
			case "B>I":
				matrix_a[1][2] = e.getValue() / mark_count[1];
				break;
			case "B>E":
				matrix_a[1][3] = e.getValue() / mark_count[1];
				break;
			case "I>I":
				matrix_a[2][2] = e.getValue() / mark_count[2];
				break;
			case "I>E":
				matrix_a[2][3] = e.getValue() / mark_count[2];
				break;
			case "E>B":
				matrix_a[3][0] = e.getValue() / mark_count[3];
				break;
			case "E>S":
				matrix_a[3][1] = e.getValue() / mark_count[3];
				break;
			default:
				break;
			}

		}

		System.out.println(state_count);
		for (double i : mark_count) {
			System.out.println(i + "  ");
		}

		double[] pi = generatePi(start_mark_count);
		ReadFile.writeFileContent(new File("pi2.txt"), pi[0] + " " + pi[1] + " " + pi[2] + " " + pi[3]);

		File matrix_a_file = new File("matrix_a.txt");
		FileWriter matrix_a_file_writer = new FileWriter(matrix_a_file);
		BufferedWriter matrix_a_bufferedWriter = new BufferedWriter(matrix_a_file_writer);

		for (int i = 0; i < matrix_a.length; i++) {
			for (int j = 0; j < matrix_a[0].length; j++) {
				System.out.print(matrix_a[i][j] + "\t");
				matrix_a_bufferedWriter.write(matrix_a[i][j] + "  ");
			}
			matrix_a_bufferedWriter.newLine();
			// System.out.println();
		}
		matrix_a_bufferedWriter.close();
	}

	public static double[] generatePi(double[] mark_count) {
		double[] pi = new double[4];
		double sum = 0;
		for (int i = 0; i < 4; i++) {
			sum += mark_count[i];
		}
		for (int i = 0; i < 4; i++) {
			pi[i] = mark_count[i] / sum;
			// System.out.println(pi[i]);
		}

		return pi;
	}

	public static void generatBmatrix() throws IOException {

		String matrix_b2_data = ReadFile.readFileContent(new File("matrix_b2.txt"));
		String mark_count = ReadFile.readFileContent(new File("mark_count.txt"));
		String[] matrix_b2_seg = matrix_b2_data.split("#");
		String[] mark_count_seg = mark_count.split(" ");
		double[] mark_count_arr = new double[4];
		for (int i = 0; i < 4; i++) {
			mark_count_arr[i] = Double.parseDouble(mark_count_seg[i]);
		}
		HashMap<String, Double> matrix_bb = new HashMap<>();

		File matrix_b3_file = new File("matrix_b3.txt");
		FileWriter matrix_b3_fw = new FileWriter(matrix_b3_file);
		BufferedWriter matrix_b3_bw = new BufferedWriter(matrix_b3_fw);

		for (String str : matrix_b2_seg) {
			double p = 0;
			switch (str.substring(str.indexOf("|") + 1, str.indexOf("|") + 2)) {
			case "S":
				p = Double.parseDouble(str.substring(str.indexOf("  ") + 1)) / mark_count_arr[0];
				break;
			case "B":
//				System.out.println(str);
				p = Double.parseDouble(str.substring(str.indexOf("  ") + 1)) / mark_count_arr[1];
				break;
			case "I":
				p = Double.parseDouble(str.substring(str.indexOf("  ") + 1)) / mark_count_arr[2];
				break;
			case "E":
				p = Double.parseDouble(str.substring(str.indexOf("  ") + 1)) / mark_count_arr[3];
				break;

			default:
				break;
			}
			matrix_bb.put(str.substring(0, str.indexOf(" ")), p);
			matrix_b3_bw.write(str.substring(0, str.indexOf(" ")) + " " + p + "#");
			matrix_b3_bw.newLine();
			
		}
		
		matrix_b3_bw.close();

	}

	public static void main(String[] args) throws IOException {
		// try {
		// wordsCountByLength(ReadFile.readFileContent(new
		// File("D:\\JavaWorkSpace\\MM\\handleCorpusResult.txt")));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// preGenerateAMatrix();
//		preHandle();
		generateAMatrixMark();
		// System.out.println(HandleCorpus.isChinese('９'));

		generateAMatrix();
		
		generatBmatrix();
	}

}
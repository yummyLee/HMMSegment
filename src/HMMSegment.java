import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author Yummy
 * @date : 2016年9月26日 上午9:47:22 I lost something,but I have more freedom now.
 */

public class HMMSegment {

	public static double[][] getMatrixA() throws IOException {

		double[][] matrix_a = new double[4][4];

		String content = ReadFile.readFileContent(new File("matrix_a.txt"));
		// System.out.println(content);
		String[] temp = content.split("  ");
		for (int i = 0; i < 16; i++) {
			matrix_a[i / 4][i % 4] = Double.parseDouble(temp[i]);
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// System.out.print(matrix_a[i][j] + "\t");
			}
			// System.out.println();
		}

		return matrix_a;
	}

	public static HashMap<String, Double> getMatrixB() throws IOException {

		String matrix_b_content = ReadFile.readFileContent(new File("matrix_b3.txt"));
		String[] matrix_b_seg = matrix_b_content.split("#");
		HashMap<String, Double> matrix_b = new HashMap<>();
		for (String str : matrix_b_seg) {
			double p = Double.parseDouble(str.substring(5));
			matrix_b.put(str.substring(0, 3), p);
			// System.out.println(str.substring(0, 3) + " " + p);
		}

		// System.out.println("想|E " + matrix_b.get("想|E "));

		return matrix_b;

	}

	public static double[] getPi() throws IOException {
		String pi_content = ReadFile.readFileContent(new File("pi2.txt"));
		String[] pi_seg = pi_content.split(" ");
		double[] pi = new double[4];
		for (int i = 0; i < 4; i++) {
			pi[i] = Double.parseDouble(pi_seg[i]);
			// System.out.println(pi[i]);
		}
		return pi;
	}

	public static String viterbi(String sentence, double[][] matrix_a, HashMap<String, Double> matrix_b, double[] pi)
			throws IOException {

		double[][] T1 = new double[4][sentence.length()];
		double[][] T2 = new double[4][sentence.length()];

		for (int i = 0; i < 4; i++) {
			Double temp = matrix_b.get(sentence.substring(0, 1) + "|" + intToString(i));
			if (temp != null) {
				T1[i][0] = pi[0] * temp;
			} else {
				T1[i][0] = 0;
			}
			T2[i][0] = 0;
		}

		// System.out.println("T1!!!!!" + sentence.length());
		for (int i = 1; i < sentence.length(); i++) {
			for (int j = 0; j < 4; j++) {
				double max = 0;
				double max2 = 0;
				double state_of_max2 = 0;
				for (int k = 0; k < 4; k++) {
					Double b_temp = matrix_b.get(sentence.substring(i, i + 1) + "|" + intToString(j));
					// System.out.print(sentence.substring(i, i + 1) + "|" +
					// intToString(j));
					if (b_temp == null) {
						b_temp = (double) 0;
						// System.out.println("error");
					}
					double t1_temp = T1[k][i - 1] * matrix_a[k][j] * b_temp;
					double t2_temp = T1[k][i - 1] * matrix_a[k][j] * b_temp;
					if (t1_temp > max) {
						max = t1_temp;
						// state_of_max = k;
					}
					if (t2_temp > max2) {
						max2 = t2_temp;
						state_of_max2 = k;
					}
				}
				T1[j][i] = max;
				// System.out.print(T1[j][i] + " ");
				T2[j][i] = state_of_max2;
			}
		}

		// for (int i = 0; i < T1.length; i++) {
		// for (int j = 0; j < T1[0].length; j++) {
		// System.out.print(T1[i][j] + " ");
		// }
		// System.out.println();
		// }
		// System.out.println();

		double[] z = new double[sentence.length()];
		// ArrayList<String> zz = new ArrayList<>();
		String zzz = "";

		for (int i = sentence.length() - 1; i < sentence.length(); i++) {
			double max = 0;
			double state_of_max = 0;
			for (int k = 0; k < 4; k++) {
				if (T1[k][i] > max) {
					max = T1[k][i];
					state_of_max = k;
				}
			}
			z[i] = state_of_max;
		}

		for (int i = sentence.length() - 1; i > 0; i--) {
			z[i - 1] = T2[(int) z[i]][i];
		}
		for (int i = 0; i < sentence.length(); i++) {
			// zz.add(intToString((int) z[i]));
			zzz += intToString((int) z[i]);
		}
		return zzz;
	}

	public static String intToString(int i) {
		if (i == 0) {
			return "S";
		} else if (i == 1) {
			return "B";
		} else if (i == 2) {
			return "I";
		} else if (i == 3) {
			return "E";
		}
		return null;
	}

	public static ArrayList<String> sentence_segment(String sentence, double[][] matrix_a,
			HashMap<String, Double> matrix_b, double[] pi) throws IOException {

		String segmark = viterbi(sentence, matrix_a, matrix_b, pi);
		// System.out.println(segmark);
		ArrayList<String> res = new ArrayList<>();
		ArrayList<Integer> segment_num = new ArrayList<>();
		int length_count = 1;
		boolean is_b = false;
		for (int i = 0; i < segmark.length(); i++) {
			String str = segmark.substring(i, i + 1);
			if (str.equals("S")) {
				if (is_b) {
					// System.err.println("分词结果有问题1");
					segment_num.add(1);
					is_b = false;
				}
				segment_num.add(1);
			} else if (str.equals("B")) {
				is_b = true;
				if (i == segmark.length() - 1) {
					segment_num.add(1);
				}
			} else if (str.equals("I")) {
				if (is_b) {
					length_count++;
				} else {
					segment_num.add(1);
					// System.err.println("分词结果有问题2");
				}
			} else if (str.equals("E")) {
				if (is_b) {
					length_count++;
					segment_num.add(length_count);
					length_count = 1;
					is_b = false;
				} else {
					segment_num.add(1);
					// System.err.println("分词结果有问题3");
				}
			}
		}
		int cur_index = 0;
		for (int i : segment_num) {
			res.add(sentence.substring(cur_index, cur_index + i));
			cur_index += i;
		}
		return res;
	}

	// 给一个文件进行分词处理
	public static String file_segment(File file) throws IOException {

		double[][] matrix_aa = getMatrixA();
		HashMap<String, Double> matrix_bb = getMatrixB();
		double[] pii = getPi();

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		File file2 = new File(file.getName().replace(".txt", "") + "_result.txt");
		FileWriter fileWriter = new FileWriter(file2);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		String str = "";
		String sentence = "";

		String final_result_str = "";

		int count = 0;
		while ((sentence = bufferedReader.readLine()) != null) {
			char[] words = sentence.toCharArray();
			for (int i = 0; i < words.length; i++) {
				char c = words[i];
				boolean is_chinese = HandleCorpus.isChinese(c);
				if (is_chinese && i != words.length - 1) {
					// System.out.println(c);
					str += String.valueOf(c);
				} else {
					// System.out.println("else"+c);
					if (i == words.length - 1) {
						str += c;
					}
					if (str.equals("")) {
						bufferedWriter.write(c);
						final_result_str += (c + "/");
						continue;
					}
					// System.out.println(str);
					ArrayList<String> res = sentence_segment(str, matrix_aa, matrix_bb, pii);
					String res_str = handleArrayList(res);
					// System.out.println(res_str);
					bufferedWriter.write(res_str);
					final_result_str += res_str;
					if (i != words.length - 1) {
						bufferedWriter.write(c);
						final_result_str += c;
					}
					str = "";
				}
			}
			bufferedWriter.newLine();
			final_result_str += ("\n");
			count++;
			if (count % 100 == 0) {
				System.out.print(count + " ");
			}

		}
		bufferedReader.close();
		bufferedWriter.close();
		System.out.println();
		return final_result_str;
	}

	public static String handleArrayList(ArrayList<String> arrayList) {
		String res = "";
		for (String str : arrayList) {
			res += (str + "/");
		}
		return res;
	}

	public static String sentence_segment_final(String sentence, double[][] matrix_a, HashMap<String, Double> matrix_b,
			double[] pi) throws IOException {
		String str = "";
		String final_res_str = "";
		char[] words = sentence.toCharArray();
		for (int i = 0; i < words.length; i++) {
			char c = words[i];
			boolean is_chinese = HandleCorpus.isChinese(c);
			if (is_chinese && i != words.length - 1) {
				// System.out.println(c);
				str += String.valueOf(c);
			} else {
				if (i == words.length - 1) {
					str += c;
				}
				// System.out.println("else"+c);
				if (str.equals("")) {
					// bufferedWriter.write(c);
					final_res_str += String.valueOf(c);
					continue;
				}
				// System.out.println(str);
				ArrayList<String> res = sentence_segment(str, matrix_a, matrix_b, pi);
				String res_str = handleArrayList(res);
				final_res_str += res_str;
				if (i != words.length - 1) {
					final_res_str += String.valueOf(c);
				}
				// System.out.println(res_str);
				// bufferedWriter.write(res_str);
				// bufferedWriter.write(c);
				str = "";
			}
		}

		return final_res_str;
	}

	public static void main(String[] args) throws IOException {
		String str1 = "全球最大的中文搜索引擎、致力于让网民更便捷地获取信息";
		String str2 = "结婚的和尚未结婚的";
		String str3 = "文本是根据一定的语言衔接和语义连贯规则而组成的整体语句或语句系统";

		System.out.println(sentence_segment_final(str1, getMatrixA(), getMatrixB(), getPi()));
		System.out.println(sentence_segment_final(str2, getMatrixA(), getMatrixB(), getPi()));
		System.out.println(sentence_segment_final(str3, getMatrixA(), getMatrixB(), getPi()));

		file_segment(new File("wait_to_segment01.txt"));
		// System.out.println(sentence_segment("推断出这三天中阿驴的一个身体状态呢"));
	}

}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadFile {
//	static String basePath = "D:\\JavaWorkSpace\\AnalogLoginTest\\示例";

	/**
	 * 查找文件夹下所有符合csv的文件
	 *
	 * @param dir
	 *            要查找的文件夹对象
	 */

	public static void findFile(File dir) throws IOException {
		File[] dirFiles = dir.listFiles();
		for (File temp : dirFiles) {
			if (!temp.isFile()) {
				findFile(temp);
			}
			// 查找指定的文件
			if (temp.isFile() && temp.getAbsolutePath().endsWith(".txt")) {
//				System.out.println(temp.isFile() + "  " + temp.getAbsolutePath());
//				String str=readFileContent(temp);
			}
		}
	}

	/**
	 * @param file
	 *            要读取的文件对象 @return 返回文件的内容
	 */

	public static String readFileContent(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		while (br.ready()) {
			sb.append(br.readLine());
		}
//		System.out.println(sb.toString());
		br.close();
		return sb.toString();
	}

	/**
	 * @param file
	 *            要写入的文件对象 @param content 要写入的文件内容
	 */

	public static void writeFileContent(File file, String content) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.flush();
		fw.close();
	}

//	public static void main(String[] args) {
//		try {
//			findFile(new File(basePath));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

import java.io.File;
import java.io.IOException;

/**
 * @author Yummy
 * @date : 2016年9月24日 下午9:17:45 I lost something,but I have more freedom now.
 */

public class Test {

	public static void isPrime(int x, int y) {
		int flag = 1;
		int i = 0;
		int j = 0;
		for (i = x; i <= y; i++) {
			flag = 1;
			for (j = 2; j < i; j++) {
				if (i % j == 0) {
					flag = 0;
					break;
				}
			}
			if (flag == 1)
				System.out.print(i + "\n");
		}
	}

	public static void main(String[] args) throws IOException {

		// isPrime(2, 10);

		/* 最大匹配 */

		// String xxxx="19980101-01-001-001/m";
		// System.out.println(xxxx.indexOf('/'));

		StandardTree standardTree = new StandardTree();
		String content;

		content = ReadFile.readFileContent(new File("1998-01-2003版-带音.txt"));

		String[] sources = content.split("  ");
		for (String str : sources) {
			// System.out.println(str+str.indexOf('/'));
			int index = str.indexOf('/');
			// System.out.println(index);
			String temp_str = str.substring(0, index);
			if (temp_str.substring(0, 1).toCharArray()[0] >= '\u4e00'
					&& temp_str.substring(0, 1).toCharArray()[0] <= '\u9fa5') {
				// System.out.println(str.substring(0, str.indexOf('/')));
				standardTree.insert(temp_str);
			}
		}

		System.out.println("insert completed");

		// System.out.println(standardTree.match("方休"));
		String str = "与影迷们进行亲切互动并畅谈影片台前幕后的趣事";
		str = str.replace(" ", "");
		System.out.println(standardTree.segment(str));

	}
}

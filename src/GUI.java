
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;

public class GUI extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Label la_file_name = null;
	private TextField tf_file_folder_name = null;
	private Button btn_select_file = null;
	private Label la_input = null;
	private Label la_result = null;
	 private TextField tf = null;
	private Button btn_hmm = null;
	private Button btn_fmm = null;
	private TextArea ta_wait_to_segment = null;
	private TextArea ta_segment_result = null;

	private MenuBar bar = null;
	private Menu fileMenu = null;
	private MenuItem openItem = null, saveItem = null, closeItem = null;
	private FileDialog openDia = null;
	// private FileDialog saveDia = null;
	private File file = null;

	private boolean is_open = false;

	public static void main(String[] args) {
		new GUI().init();

	}

	public void init() {
		// 窗口关闭事件
		this.setResizable(false);
		this.setTitle("简单的中文分词");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setLayout(new FlowLayout());

		la_file_name = new Label("选择文件:");
		tf_file_folder_name = new TextField(23);
		btn_select_file = new Button("选择文件");

		la_input = new Label("输入文本:");
		la_result = new Label("分词结果");

		setTf(new TextField(34));
		btn_hmm = new Button("HMM");
		btn_fmm = new Button("FMM");
		ta_wait_to_segment = new TextArea("", 5, 58);
		ta_segment_result = new TextArea("", 13, 58);
		// ta_segment_result.setLi
		// 按键响应事件

		btn_select_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDia.setVisible(true);

				String dirPath = openDia.getDirectory();
				String fileName = openDia.getFile();

				if (dirPath == null || fileName == null) {
					return;
				}

				ta_wait_to_segment.setText("");
				file = new File(dirPath, fileName);
				tf_file_folder_name.setText(file.getPath());
				is_open = true;

				try {
					BufferedReader bufr = new BufferedReader(new FileReader(file));
					String line = null;

					while ((line = bufr.readLine()) != null) {
						ta_wait_to_segment.append(line + "\r\n");
					}
					bufr.close();

				} catch (IOException ex) {
					throw new RuntimeException("读取失败");
				}
			}
		});

		btn_hmm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = ta_wait_to_segment.getText();
				String res = "";
				try {
					if (is_open) {
						res = HMMSegment.file_segment(new File(file.getPath()));
						System.out.println("open file");
						is_open = false;
					} else {
						res = HMMSegment.sentence_segment_final(str, HMMSegment.getMatrixA(), HMMSegment.getMatrixB(),
								HMMSegment.getPi());
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.err.println("出错");
					e1.printStackTrace();
				}
				// System.out.println(res);
				ta_segment_result.setText("HMM分词结果：\n" + res);
				// 弹窗显示结果
				// JOptionPane.showMessageDialog(null, s);
			}
		});
		btn_fmm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = ta_wait_to_segment.getText();
				String res = "";
				try {
					StandardTree standardTree = new StandardTree();
					String content;

					content = ReadFile.readFileContent(new File("1998-01-2003版-带音.txt"));

					String[] sources = content.split("  ");
					for (String str2 : sources) {
						// System.out.println(str+str.indexOf('/'));
						int index = str2.indexOf('/');
						// System.out.println(index);
						String temp_str = str2.substring(0, index);
						if (temp_str.substring(0, 1).toCharArray()[0] >= '\u4e00'
								&& temp_str.substring(0, 1).toCharArray()[0] <= '\u9fa5') {
							// System.out.println(str.substring(0,
							// str.indexOf('/')));
							standardTree.insert(temp_str);
						}
					}

					System.out.println("insert completed");
					res = standardTree.segment(str).toString();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// System.out.println(res);
				ta_segment_result.setText("FMM分词结果：\n" + res);
				// 弹窗显示结果
				// JOptionPane.showMessageDialog(null, s);
			}
		});

		/* 打开文件 */
		bar = new MenuBar();
		fileMenu = new Menu("文件");

		openItem = new MenuItem("打开");
		saveItem = new MenuItem("保存");
		closeItem = new MenuItem("退出");

		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(closeItem);
		bar.add(fileMenu);

		// this.setMenuBar(bar);

		openDia = new FileDialog(this, "我要打开", FileDialog.LOAD);
//		saveDia = new FileDialog(this, "我要保存", FileDialog.SAVE);

		// myEvent();

		this.setVisible(true);

		add(la_file_name);
		add(tf_file_folder_name);
		add(btn_select_file);

		// add(tf);
		add(btn_hmm);
		add(btn_fmm);
		add(la_input);
		add(ta_wait_to_segment);
		add(la_result);
		add(ta_segment_result);
		setBounds(400, 300, 450, 460); // 设置窗体的位置和尺寸
		setVisible(true);
	}

	public TextField getTf() {
		return tf;
	}

	public void setTf(TextField tf) {
		this.tf = tf;
	}

	// private void myEvent() {
	// // TODO Auto-generated method stub
	// saveItem.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// if (file == null) {
	// saveDia.setVisible(true);
	//
	// String dirPath = saveDia.getDirectory();
	// String fileName = saveDia.getFile();
	//
	// if (dirPath == null || fileName == null) {
	// return;
	// }
	// file = new File(dirPath, fileName);
	// }
	//
	// try {
	// BufferedWriter bufw = new BufferedWriter(new FileWriter(file));
	// String text = ta_wait_to_segment.getText();
	// bufw.write(text);
	// bufw.flush();
	// bufw.close();
	// } catch (IOException ex) {
	// throw new RuntimeException();
	// }
	// }
	// });
	//
	// btn_select_file.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// openDia.setVisible(true);
	// String dirPath = openDia.getDirectory();
	// String fileName = openDia.getFile();
	//
	// if (dirPath == null || fileName == null) {
	// return;
	// }
	//
	// ta_wait_to_segment.setText("");
	// file = new File(dirPath, fileName);
	//
	// try {
	// BufferedReader bufr = new BufferedReader(new FileReader(file));
	// String line = null;
	//
	// while ((line = bufr.readLine()) != null) {
	// ta_wait_to_segment.append(line + "\r\n");
	// }
	// bufr.close();
	// } catch (IOException ex) {
	// throw new RuntimeException("读取失败");
	// }
	// }
	// });
	//
	// closeItem.addActionListener(new ActionListener() {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// // TODO Auto-generated method stub
	// System.exit(0);
	// }
	// });
	//
	// this.addWindowListener(new WindowAdapter() {
	// public void windowClosing(WindowEvent e) {
	// System.exit(0);
	// }
	// });
	// }
}

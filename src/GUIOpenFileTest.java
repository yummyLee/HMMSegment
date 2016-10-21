import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GUIOpenFileTest {

	/**
	 * @param args
	 */
	private Frame f;
	private MenuBar bar;
	private TextArea ta;
	private Menu fileMenu;
	private MenuItem openItem, saveItem, closeItem;

	private FileDialog openDia, saveDia;

	private File file;

	GUIOpenFileTest() {
		init();
	}

	public void init() {
		// TODO Auto-generated method stub
		f = new Frame("my window");
		f.setBounds(300, 100, 650, 600);

		bar = new MenuBar();

		ta = new TextArea();

		fileMenu = new Menu("文件");

		openItem = new MenuItem("打开");
		saveItem = new MenuItem("保存");
		closeItem = new MenuItem("退出");

		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(closeItem);
		bar.add(fileMenu);

		f.setMenuBar(bar);

		openDia = new FileDialog(f, "我要打开", FileDialog.LOAD);
		saveDia = new FileDialog(f, "我要保存", FileDialog.SAVE);

		f.add(ta);
		myEvent();

		f.setVisible(true);

	}

	private void myEvent() {
		// TODO Auto-generated method stub
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file == null) {
					saveDia.setVisible(true);

					String dirPath = saveDia.getDirectory();
					String fileName = saveDia.getFile();

					if (dirPath == null || fileName == null) {
						return;
					}
					file = new File(dirPath, fileName);
				}

				try {
					BufferedWriter bufw = new BufferedWriter(new FileWriter(file));
					String text = ta.getText();
					bufw.write(text);
					bufw.flush();
					bufw.close();
				} catch (IOException ex) {
					throw new RuntimeException();
				}
			}
		});

		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDia.setVisible(true);
				String dirPath = openDia.getDirectory();
				String fileName = openDia.getFile();

				if (dirPath == null || fileName == null) {
					return;
				}

				ta.setText("");
				file = new File(dirPath, fileName);

				try {
					BufferedReader bufr = new BufferedReader(new FileReader(file));
					String line = null;

					while ((line = bufr.readLine()) != null) {
						ta.append(line + "\r\n");
					}
					bufr.close();
				} catch (IOException ex) {
					throw new RuntimeException("读取失败");
				}
			}
		});

		closeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GUIOpenFileTest();

	}

}
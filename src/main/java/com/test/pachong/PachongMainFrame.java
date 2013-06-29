package com.test.pachong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class PachongMainFrame extends JFrame implements ActionListener,
		Runnable {

	JList list;
	JTextField targetField;
	JButton startButton;
	JPanel northPanel;
	JPanel southPanel;
	JProgressBar pbar;
	JPanel centerPanel;
	Vector<String> v = new Vector<String>();
	JPopupMenu pop;
	Thread thread;
	JTabbedPane tabPane;
	JPanel fileListPanel;
	ThreadManager thm;

	public PachongMainFrame() {
         /*
          长春理工大学光电信息学院 猫猫
         qq382758656
        */

		setTitle("爬虫程序");
		setLayout(new BorderLayout());
		
		
		list = new JList();
		list.setBackground(new Color(235, 203, 139));
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setBorder(BorderFactory.createLineBorder(Color.black));
		targetField = new JTextField(30);

		targetField.addActionListener(this);

		northPanel = new JPanel();
		northPanel.setBackground(new Color(235, 203, 139));
		centerPanel = new JPanel();
		centerPanel.setBackground(new Color(235, 203, 139));

		southPanel = new JPanel();
		southPanel.setBackground(new Color(235, 203, 139));
		southPanel.setLayout(new BorderLayout());
		pbar = new JProgressBar();
		// #ebcb8b
		pbar.setBackground(new Color(235, 203, 139));
		pbar.setForeground(Color.black);

		southPanel.add(pbar);// 添加进度条
		add(southPanel, BorderLayout.SOUTH);

		startButton = new JButton("开始");
		startButton.addActionListener(this);

		northPanel.add(new JLabel("网址："));
		northPanel.add(targetField);
		northPanel.add(startButton);

		centerPanel.setBorder(BorderFactory.createLineBorder(Color.green, 1));
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setBackground(new Color(235, 203, 139));
		centerPanel.add(list);
		add(centerPanel, BorderLayout.CENTER);
		add(northPanel, BorderLayout.NORTH);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		pop = new JPopupMenu();

		JMenuItem itemAccItem = new JMenuItem("访问网址");
		itemAccItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String url = v.elementAt(list.getSelectedIndex());
				openBrower(url);
			}
		});
		JMenuItem itemCopyItem = new JMenuItem("复制地址");
		itemCopyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				copyAddress();
			}
		});

		JMenuItem itemCopyDownload = new JMenuItem("下载资源");
		itemCopyDownload.addActionListener(new ActionListener() {

			DownloadThread dt = null;

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Object valueItems[] = list.getSelectedValues();
				for (int i = 0; i < valueItems.length; i++) {
					System.out.println("选中资源连接:" + valueItems[i]);

					// 把下载线程装入容器集合便于管理
					dt = new DownloadThread(valueItems[i] + "");
					dt.start();

					Tools.downThreads.add(dt);
				}

				addFileListToPanel();
				validate();
			}
		});
		pop.add(itemAccItem);
		pop.add(itemCopyItem);
		pop.add(itemCopyDownload);

		tabPane = new JTabbedPane();
		tabPane.setTabPlacement(SwingConstants.LEFT);

		fileListPanel = new JPanel();
		fileListPanel.setBackground(new Color(235, 203, 139));
		fileListPanel.setLayout(new BorderLayout());
		tabPane.setPreferredSize(new Dimension(230, this.getHeight() - 10));
		tabPane.add(fileListPanel, "下载列表");

		add(tabPane, BorderLayout.WEST);
		thm = new ThreadManager();
		thm.start();
		setVisible(true);
		setBounds(120, 80, 800, 600);
		validate();

	}

	public void openBrower(String url) {

		System.out.println("选中地址：" + url);
		try {
			Runtime.getRuntime().exec("cmd.exe /c start " + url);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void copyAddress() {

		Clipboard clipboard = getToolkit().getSystemClipboard();

		StringSelection text = new StringSelection(v.elementAt(list
				.getSelectedIndex()));
		clipboard.setContents(text, null);

	}

	public void testGetImage(String url) {
		try {
			Parser parser = new Parser(url);
			NodeList list = parser.parse(null);
			for (int i = 0; i < list.size(); i++) {
				Node node = list.elementAt(i);
				processMyNodesImg(node);

			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void processMyNodesImg(Node node) {

		if (node instanceof ImageTag) {
			// 如果是图片
			ImageTag img = (ImageTag) node;
			v.add(img.getImageURL());

			System.out.println("图片url:" + img.getImageURL());

		}
		if (node instanceof LinkTag) {
			// 如果是超链接
			LinkTag link = (LinkTag) node;
			if((!link.getLink().equals("") || link.getLink()!=null )&&link.getLink().startsWith("http://"))
			{
			v.add(link.getLink());
			System.out.println("linktag.getLink:" + link.getLink());
			}
			/*
			 * System.out.println("linktag.getLinkText:"+link.getLinkText());
			 * System.out.println("linktag.getPage:"+link.getPage());
			 * System.out.
			 * println("linktag.getStringText:"+link.getStringText());
			 * System.out.println("linktag.getParent:"+link.getParent());
			 */

		} else if (node instanceof TagNode) {

			// downcast to TagNode
			TagNode tag = (TagNode) node;
			// do whatever processing you want with the tag itself
			// ...
			// process recursively (nodes within nodes) via getChildren()
			NodeList nl = tag.getChildren();
			if (null != nl)
				try {
					for (NodeIterator i = nl.elements(); i.hasMoreNodes();)
						processMyNodesImg(i.nextNode());
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == startButton || e.getSource() == targetField) {

			thread = new Thread(this);
			thread.start();

		}
	}

	public JPanel addFileListToPanel() {

		String listmodel[] = new String[Tools.downThreads.size()];
		for (int i = 0; i < listmodel.length; i++) {
			listmodel[i] = Tools.downThreads.get(i).getName();
		}

		JList fileList = new JList(listmodel);
		fileList.setBackground(new Color(235, 203, 139));
		fileListPanel.removeAll();
		fileListPanel.setBackground(new Color(235, 203, 139));
		fileListPanel.add(fileList);
		validate();
		return fileListPanel;
	}

	public static void main(String[] args) {
		PachongMainFrame chframe = new PachongMainFrame();
		// chframe.getURLFileName("http://wwwafa/a.gif");
	}

	@Override
	public void run() {
		pbar.setIndeterminate(true);
		v.removeAllElements();
		centerPanel.removeAll();

		testGetImage(targetField.getText().trim());
		list = new JList(v);
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(new JScrollPane(list));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.isPopupTrigger()) {
					pop.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		validate();
		pbar.setIndeterminate(false);
	}

	class ThreadManager extends Thread {
		// 线程管理类
		DownloadThread dt = null;

		public void run() {
			while (true) {

				for (int i = 0; i < Tools.downThreads.size(); i++) {
					dt = Tools.downThreads.get(i);
					if (dt.isOver) {
						Tools.downThreads.remove(i);
						addFileListToPanel();
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}
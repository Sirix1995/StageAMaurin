/*
 * Copyright (C) 2014 GroIMP Developer Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp;

import static org.jocl.CL.clGetDeviceInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import de.grogra.pf.boot.Main;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.util.I18NBundle;

/**
 * Opens a panel to provide basic system informations.
 * 
 */
public class SystemInfoDialog extends JDialog {

	private static final long serialVersionUID = 12355478421L;

	private final static double MEGS = 1048576.0;
	private static final String[] LIB_EXTENSIONS = {"dll","so", "jnilib"};

	private static final String EXPORT_FILE_NAME = System.getProperty("user.home") +System.getProperty("file.separator") + "GroIMPSystemInfo.txt"; 
	
	private static String LIB_SEARCH_PATH = System.getProperty("java.ext.dirs")+File.pathSeparatorChar+
			System.getProperty("java.library.path")+File.pathSeparatorChar+System.getProperty("java.class.path");
	
	private final static int TEXT_WIDTH = 46;
	
	private static SystemInfoDialog dialog;
	private static I18NBundle thisI18NBundle;
	private JButton cancelButton, exportButton;
	private final static Font defaultFont = new Font("Dialog", 0, 12);

	private final StringBuffer logSB = new StringBuffer();

	private static String javaVersion = "";

	private void openPanel(Context ctx) {
		setModal(true);
		setResizable(true);
		setSize(650, 700);
		setLocationByPlatform(true);
		setTitle(logBundle("systeminfo.dilog.title.Name"));
		logText(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));

		makeLayout(ctx);
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		setVisible(true);
	}

	private void makeLayout(Context ctx) {
		JPanel okCancelPanel = new JPanel();
		okCancelPanel.setLayout(new GridLayout(1, 2, 8, 0));
		cancelButton = new JButton(thisI18NBundle.getString("systeminfo.dilog.cancel.Name"));
		cancelButton.setFont(defaultFont);
		exportButton = new JButton(thisI18NBundle.getString("systeminfo.dilog.export.Name"));
		exportButton.setFont(defaultFont);
		okCancelPanel.add(exportButton);
		okCancelPanel.add(cancelButton);

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createHorizontalStrut(6));
		p2.add(okCancelPanel);
		p2.add(Box.createHorizontalStrut(6));

		// main pane
		UIToolkit ui = UIToolkit.get(ctx);
		JTabbedPane tabPane = (JTabbedPane) ui.createTabbedPane(new String[] {
				thisI18NBundle.getString("systeminfo.dilog.tab0.Name"),
				thisI18NBundle.getString("systeminfo.dilog.tab1.Name"),
				thisI18NBundle.getString("systeminfo.dilog.tab2.Name"),
				thisI18NBundle.getString("systeminfo.dilog.tab3.Name"),
				thisI18NBundle.getString("systeminfo.dilog.tab4.Name")},
				new Object[] { 
					getSystmePropertyPanel(), getJavaPropertyPanel(), getGroimpPanel(ctx), getLibrariesPanel(ui), getFluxSettingsPanel() 
				});

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		p1.add(new JScrollPane(tabPane));
		p1.add(new JSeparator());
		p1.add(Box.createVerticalStrut(4));
		p1.add(p2);
		p1.add(Box.createVerticalStrut(4));

		add(p1);
	}

	public static void createInfoPanel(Item item, Object info, Context ctx) {
		thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();
		dialog = new SystemInfoDialog();
		dialog.openPanel(ctx);
	}

	private JPanel getSystmePropertyPanel() {
		int max = 6;
		JLabel[] p = new JLabel[max];
		JLabel[] pv = new JLabel[max];

		String s = "";
		s = logBundle("systeminfo.dilog.property12.Name");
		p[0] = new JLabel(s + " : ");
		p[0].setFont(defaultFont);
		pv[0] = new JLabel(logText(System.getProperty(s)));
		s = logBundle("systeminfo.dilog.property13.Name");
		p[1] = new JLabel(s + " : ");
		p[1].setFont(defaultFont);
		pv[1] = new JLabel(logText(System.getProperty(s)));
		s = logBundle("systeminfo.dilog.property14.Name");
		p[2] = new JLabel(s + " : ");
		p[2].setFont(defaultFont);
		pv[2] = new JLabel(logText(System.getProperty(s)));
		Runtime rt = Runtime.getRuntime();
		p[3] = new JLabel(logBundle("systeminfo.dilog.property15.Name") + " : ");
		p[3].setFont(defaultFont);
		pv[3] = new JLabel(logText(""+rt.availableProcessors()));
		s = logBundle("systeminfo.dilog.property19.Name");
		p[4] = new JLabel(s + " : ");
		p[4].setFont(defaultFont);
		pv[4] = new JLabel(logText(System.getProperty(s)));
		s = logBundle("systeminfo.dilog.property20.Name");
		p[5] = new JLabel(s + " : ");
		p[5].setFont(defaultFont);
		pv[5] = new JLabel(logText(System.getProperty(s)));
		
		logText(System.getProperty("line.separator"));
		
		JPanel p0 = new JPanel();
		GroupLayout layout = new GroupLayout(p0);
		p0.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(p[0]).addComponent(p[1])
								.addComponent(p[2]).addComponent(p[3])
								.addComponent(p[4]).addComponent(p[5]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(pv[0]).addComponent(pv[1])
								.addComponent(pv[2]).addComponent(pv[3])
								.addComponent(pv[4]).addComponent(pv[5])));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[0]).addComponent(pv[0]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[1]).addComponent(pv[1]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[2]).addComponent(pv[2]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[3]).addComponent(pv[3]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[4]).addComponent(pv[4]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[5]).addComponent(pv[5]))
				);
		return p0;
	}
	
	private JPanel getJavaPropertyPanel() {
		int max = 15;
		JLabel[] p = new JLabel[max];
		JLabel[] pv = new JLabel[max];
		String s = "";
		for (int i = 0; i < 12; i++) {
			s = logBundle("systeminfo.dilog.property" + i + ".Name");
			p[i] = new JLabel(s + " : ");
			p[i].setFont(defaultFont);
			pv[i] = new JLabel(logText(System.getProperty(s)));
			if(s.equals("sun.arch.data.model")) {
				javaVersion = System.getProperty(s);
				if (javaVersion.equals("32")) {
					pv[i] = new JLabel(logText(System.getProperty(s) + "! Consider a 64-bit Java version."));
				}
			}
		}

		Runtime rt = Runtime.getRuntime();
		p[12] = new JLabel(logBundle("systeminfo.dilog.property16.Name") + " : ");
		p[12].setFont(defaultFont);
		pv[12] = new JLabel(logText(""+rt.freeMemory()/MEGS));
		p[13] = new JLabel(logBundle("systeminfo.dilog.property17.Name") + " : ");
		p[13].setFont(defaultFont);
		pv[13] = new JLabel(logText(""+rt.maxMemory()/MEGS));
		p[14] = new JLabel(logBundle("systeminfo.dilog.property18.Name") + " : ");
		p[14].setFont(defaultFont);
		pv[14] = new JLabel(logText(""+rt.totalMemory()/MEGS));
		logText(System.getProperty("line.separator"));
		
		JPanel p0 = new JPanel();
		GroupLayout layout = new GroupLayout(p0);
		p0.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(p[0]).addComponent(p[1])
								.addComponent(p[2]).addComponent(p[3])
								.addComponent(p[4]).addComponent(p[5])
								.addComponent(p[6]).addComponent(p[7])
								.addComponent(p[8]).addComponent(p[9])
								.addComponent(p[10]).addComponent(p[11])
								.addComponent(p[12]).addComponent(p[13])
								.addComponent(p[14]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(pv[0]).addComponent(pv[1])
								.addComponent(pv[2]).addComponent(pv[3])
								.addComponent(pv[4]).addComponent(pv[5])
								.addComponent(pv[6]).addComponent(pv[7])
								.addComponent(pv[8]).addComponent(pv[9])
								.addComponent(pv[10]).addComponent(pv[11])
								.addComponent(pv[12]).addComponent(pv[13])
								.addComponent(pv[14])));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[0]).addComponent(pv[0]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[1]).addComponent(pv[1]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[2]).addComponent(pv[2]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[3]).addComponent(pv[3]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[4]).addComponent(pv[4]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[5]).addComponent(pv[5]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[6]).addComponent(pv[6]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[7]).addComponent(pv[7]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[8]).addComponent(pv[8]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[9]).addComponent(pv[9]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[10]).addComponent(pv[10]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[11]).addComponent(pv[11]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[12]).addComponent(pv[12]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[13]).addComponent(pv[13]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[14]).addComponent(pv[14]))
				);
		return p0;
	}


	private JPanel getGroimpPanel(Context ctx) {
		int max = 6;
		JLabel[] p = new JLabel[max];
		JLabel[] pv = new JLabel[max];

		String s = isDeveloperVersion() ? 
				thisI18NBundle.getString("systeminfo.dilog.groimp2.Name") : 
				thisI18NBundle.getString("systeminfo.dilog.groimp3.Name");

		p[0] = new JLabel(logBundle("systeminfo.dilog.groimp0.Name") + " : ");
		p[0].setFont(defaultFont);
		pv[0] = new JLabel(logText(thisI18NBundle.getString("app.version.Name") + " (" + s + ")"));
		
		p[1] = new JLabel(logBundle("systeminfo.dilog.groimp9.Name") + " : ");
		p[1].setFont(defaultFont);
		pv[1] = new JLabel(logText("" + thisI18NBundle.getString("build-date")));
		
		p[2] = new JLabel(logBundle("systeminfo.dilog.groimp1.Name") + " : ");
		p[2].setFont(defaultFont);
		pv[2] = new JLabel(logText("64"));
		p[3] = new JLabel(logBundle("systeminfo.dilog.groimp4.Name") + " : ");
		p[3].setFont(defaultFont);
		pv[3] = new JLabel(logText(System.getProperty(thisI18NBundle.getString("systeminfo.dilog.groimp4.Name"))));
		p[4] = new JLabel(logBundle("systeminfo.dilog.groimp8.Name") + " : ");
		p[4].setFont(defaultFont);
		pv[4] = new JLabel(logText( Main.getProperty("boot") ));
		p[5] = new JLabel(logBundle("systeminfo.dilog.groimp5.Name") + " : ");
		p[5].setFont(defaultFont);
		pv[5] = new JLabel(logText(System.getProperty(thisI18NBundle.getString("systeminfo.dilog.groimp5.Name"))));
		
		JPanel p0 = new JPanel();
		GroupLayout layout = new GroupLayout(p0);
		p0.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(p[0]).addComponent(p[1]).addComponent(p[3]).addComponent(p[2]).addComponent(p[4]).addComponent(p[5]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(pv[0]).addComponent(pv[1]).addComponent(pv[3]).addComponent(pv[2]).addComponent(pv[4]).addComponent(pv[5])));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[0]).addComponent(pv[0]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[1]).addComponent(pv[1]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[3]).addComponent(pv[3]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[2]).addComponent(pv[2]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[4]).addComponent(pv[4]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[5]).addComponent(pv[5])));
		logText(System.getProperty("line.separator"));
		
		JPanel p1 = new JPanel();
		p1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p1.add(p0, c);
		c.gridx = 0;
		c.gridy = 2;
		p1.add(Box.createVerticalStrut(275), c);
		return p1;
	}

	private String logText(String s) {
		logSB.append(s + System.getProperty("line.separator"));
		return s;
	}
	private String logBundle(String s) {
		s = thisI18NBundle.getString(s);
		logSB.append(s + " ");
		return s;
	}
	
	private void export() {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(new File(EXPORT_FILE_NAME));
			fileWriter.write(logSB.toString());
			fileWriter.close();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, thisI18NBundle.getString("systeminfo.dilog.export1.Name"), JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				fileWriter.close();
				JOptionPane.showMessageDialog(null, thisI18NBundle.getString("systeminfo.dilog.export0.Name") + System.getProperty("line.separator") + EXPORT_FILE_NAME);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, ex, thisI18NBundle.getString("systeminfo.dilog.export1.Name"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean isDeveloperVersion() {
		return System.getProperty("user.dir").contains("Platform-Core");
	}

	private JPanel getLibrariesPanel(UIToolkit ui) {
		JTabbedPane tabPane = (JTabbedPane) ui.createTabbedPane(new String[] {
				thisI18NBundle.getString("systeminfo.dilog.libtab3.Name"),
				thisI18NBundle.getString("systeminfo.dilog.libtab0.Name"),
				thisI18NBundle.getString("systeminfo.dilog.libtab1.Name"),
				thisI18NBundle.getString("systeminfo.dilog.libtab2.Name") },
				new Object[] {getPathsPanel(), getInstalledLibPanel(), getJOGLPanel(), getJOCLPanel() });
		JPanel p = new JPanel();
		p.add(tabPane);
		return p;
	}

	private JPanel getPathsPanel() {
		String s = "empty";
		
		JLabel p0 = new JLabel(logBundle("systeminfo.dilog.instlib0.Name"));
		logText(System.getProperty("line.separator"));
		JLabel p2 = new JLabel(logBundle("systeminfo.dilog.instlib2.Name") + " : ");
		p2.setFont(defaultFont);
		s = System.getProperty("java.ext.dirs");
		if(s==null || s.equals("")) s= "empty";
		JTextArea pv2 = new JTextArea(logText(s));
		pv2.setColumns(TEXT_WIDTH);
		pv2.setLineWrap(true);
		pv2.setRows(5);
		pv2.setWrapStyleWord(true);
		pv2.setEditable(false);
		
		JLabel p3 = new JLabel(logBundle("systeminfo.dilog.instlib3.Name") + " : ");
		p3.setFont(defaultFont);
		s = System.getProperty("java.library.path");
		if(s==null || s.equals("")) s= "empty";
		JTextArea pv3 = new JTextArea(logText(s));
		pv3.setColumns(TEXT_WIDTH);
		pv3.setLineWrap(true);
		pv3.setRows(5);
		pv3.setWrapStyleWord(true);
		pv3.setEditable(false);
		
		JLabel p4 = new JLabel(logBundle("systeminfo.dilog.instlib4.Name") + " : ");
		p4.setFont(defaultFont);
		s = System.getProperty("java.class.path");
		if(s==null || s.equals("")) s= "empty";
		JTextArea pv4 = new JTextArea(logText(s));
		pv4.setColumns(TEXT_WIDTH);
		pv4.setLineWrap(true);
		pv4.setRows(5);
		pv4.setWrapStyleWord(true);
		pv4.setEditable(false);
		
		JLabel p5 = new JLabel(logBundle("systeminfo.dilog.instlib5.Name") + " : ");
		p5.setFont(defaultFont);
		s = Main.getProperty("extensionDirectory");
		if(s==null || s.equals("")) s= "empty";
		JLabel pv5 = new JLabel(logText(s));
		logText(System.getProperty("line.separator"));

		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p.add(p0, c);

		c.gridx = 0;
		c.gridy = 2;
		p.add(p2, c);
		c.gridx = 0;
		c.gridy = 3;
		p.add(new JScrollPane(pv2), c);

		c.gridx = 0;
		c.gridy = 4;
		p.add(p3, c);
		c.gridx = 0;
		c.gridy = 5;
		p.add(new JScrollPane(pv3), c);

		c.gridx = 0;
		c.gridy = 6;
		p.add(p4, c);
		c.gridx = 0;
		c.gridy = 7;
		p.add(new JScrollPane(pv4), c);

		c.gridx = 0;
		c.gridy = 8;
		p.add(p5, c);
		c.gridx = 0;
		c.gridy = 9;
		p.add(pv5, c);
		return p;
	}

	private JPanel getInstalledLibPanel() {
		ClassLoader appLoader = ClassLoader.getSystemClassLoader();
		ClassLoader currentLoader = SystemInfoDialog.class.getClassLoader();

		ClassLoader[] loaders = new ClassLoader[] { appLoader, currentLoader };
		String[] libraries = {""};
		try {
			libraries = ClassScope.getLoadedLibraries(loaders);
		} catch (RuntimeException e) {
			libraries[0] = e.toString();
			libraries[1] = "Not possible to get the List of loaded libraries for jdk9+ ondwards!";
		}
		StringBuffer sb0 = new StringBuffer();
		for (String library : libraries) {
			sb0.append(library+System.getProperty("line.separator"));
		}
		
		JLabel p0 = new JLabel(logBundle("systeminfo.dilog.instlib6.Name") + " : ");
		JTextArea textArea0 = new JTextArea();
		textArea0.setColumns(TEXT_WIDTH);
		textArea0.setLineWrap(true);
		textArea0.setRows(15);
		textArea0.setWrapStyleWord(true);
		textArea0.setEditable(false);
		textArea0.setText(logText(sb0.toString()));
		logText(System.getProperty("line.separator"));
		
		StringTokenizer tokenizer = new StringTokenizer(Main.getProperty("extensionDirectory")+":"+LIB_SEARCH_PATH, ";:");
		StringBuffer sb1 = new StringBuffer();
		while (tokenizer.hasMoreElements()) {
			File folder = new File((String)tokenizer.nextElement());
			File[] listOfFiles = folder.listFiles();
			if(listOfFiles != null && listOfFiles.length>0) {
				for (File file : listOfFiles) {
					if(file.isFile() && isFileOfInterest(file.getName())) {
						sb1.append(" "+file.getAbsolutePath()+"("+file.length()+")"+System.getProperty("line.separator"));
					}
				}
			}
		}
		
		JLabel p1 = new JLabel(logBundle("systeminfo.dilog.instlib1.Name") + " : ");
		JTextArea textArea1 = new JTextArea();
		textArea1.setColumns(TEXT_WIDTH);
		textArea1.setLineWrap(true);
		textArea1.setRows(15);
		textArea1.setWrapStyleWord(true);
		textArea1.setEditable(false);
		textArea1.setText(logText(sb1.toString()));
		logText(System.getProperty("line.separator"));

		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p.add(p0, c);
		c.gridx = 0;
		c.gridy = 2;
		p.add(new JScrollPane(textArea0), c);
		c.gridx = 0;
		c.gridy = 3;
		p.add(Box.createVerticalStrut(5), c);
		
		c.gridx = 0;
		c.gridy = 4;
		p.add(p1, c);
		c.gridx = 0;
		c.gridy = 5;
		p.add(new JScrollPane(textArea1), c);
		c.gridx = 0;
		c.gridy = 6;
		p.add(Box.createVerticalStrut(5), c);
		return p;
	}

	
	private JPanel getFluxSettingsPanel() {
		int max = 14;
		JLabel[] p = new JLabel[max];
		JLabel[] pv = new JLabel[max];
		JLabel[] l = new JLabel[3];

		l[0] = new JLabel(thisI18NBundle.getString("systeminfo.dilog.fluxsettingsgroups0.Name"));
		l[1] = new JLabel(thisI18NBundle.getString("systeminfo.dilog.fluxsettingsgroups1.Name"));
		l[2] = new JLabel(thisI18NBundle.getString("systeminfo.dilog.fluxsettingsgroups2.Name"));
		JLabel ln = new JLabel();
		
		p[0] = new JLabel(logBundle("systeminfo.dilog.fluxsettings0.Name") + " : ");
		p[0].setFont(defaultFont);
		pv[0] = new JLabel(logText(FluxSettings.getModelSpectralLambdaStep() + " nm"));
		p[1] = new JLabel(logBundle("systeminfo.dilog.fluxsettings1.Name") + " : ");
		p[1].setFont(defaultFont);
		pv[1] = new JLabel(logText(""+FluxSettings.getModelFlatness()));
		p[2] = new JLabel(logBundle("systeminfo.dilog.fluxsettings13.Name") + " : ");
		p[2].setFont(defaultFont);
		pv[2] = new JLabel(logText(""+FluxSettings.getOCLRayOffset()));
		p[3] = new JLabel(logBundle("systeminfo.dilog.fluxsettings2.Name") + " : ");
		p[3].setFont(defaultFont);
		pv[3] = new JLabel(logText(""+FluxSettings.getOCLInitialSampleCount()));
		p[4] = new JLabel(logBundle("systeminfo.dilog.fluxsettings3.Name") + " : ");
		p[4].setFont(defaultFont);
		pv[4] = new JLabel(logText(""+FluxSettings.getOCLMaximumSampleCount()));
		p[5] = new JLabel(logBundle("systeminfo.dilog.fluxsettings4.Name") + " : ");
		p[5].setFont(defaultFont);
		pv[5] = new JLabel(logText(""+FluxSettings.getOCLPreferredDuration()));
		p[6] = new JLabel(logBundle("systeminfo.dilog.fluxsettings5.Name") + " : ");
		p[6].setFont(defaultFont);
		pv[6] = new JLabel(logText((FluxSettings.getOCLPrecompile()?"Enabled":"Disabled")));
		p[7] = new JLabel(logBundle("systeminfo.dilog.fluxsettings6.Name") + " : ");
		p[7].setFont(defaultFont);
		pv[7] = new JLabel(logText((FluxSettings.getOCLGPU()?"Enabled":"Disabled")));
		p[8] = new JLabel(logBundle("systeminfo.dilog.fluxsettings7.Name") + " : ");
		p[8].setFont(defaultFont);
		pv[8] = new JLabel(logText((FluxSettings.getOCLMultiGPU()?"Enabled":"Disabled")));
		p[9] = new JLabel(logBundle("systeminfo.dilog.fluxsettings8.Name") + " : ");
		p[9].setFont(defaultFont);
		pv[9] = new JLabel(logText((FluxSettings.getOCLPerformance()==0?"Fast Build":"Fast Trace")));
		p[10] = new JLabel(logBundle("systeminfo.dilog.fluxsettings9.Name") + " : ");
		p[10].setFont(defaultFont);
		pv[10] = new JLabel(logText(""+FluxSettings.getRenderDepth()));
		p[11] = new JLabel(logBundle("systeminfo.dilog.fluxsettings10.Name") + " : ");
		p[11].setFont(defaultFont);
		pv[11] = new JLabel(logText(""+FluxSettings.getRenderMinPower()));
		p[12] = new JLabel(logBundle("systeminfo.dilog.fluxsettings11.Name") + " : ");
		p[12].setFont(defaultFont);
		pv[12] = new JLabel(logText((FluxSettings.getRenderSpectral()?"Enabled":"Disabled")));
		p[13] = new JLabel(logBundle("systeminfo.dilog.fluxsettings12.Name") + " : ");
		p[13].setFont(defaultFont);
		pv[13] = new JLabel(logText((FluxSettings.getRenderDispersion()?"Enabled":"Disabled")));
		logText(System.getProperty("line.separator"));

		JPanel p0 = new JPanel();
		GroupLayout layout = new GroupLayout(p0);
		p0.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(l[0])
								.addComponent(p[0]).addComponent(p[1]).addComponent(p[2])
								.addComponent(l[1])
								.addComponent(p[3])
								.addComponent(p[4]).addComponent(p[5])
								.addComponent(p[6]).addComponent(p[7])
								.addComponent(p[8]).addComponent(p[9])
								.addComponent(l[2])
								.addComponent(p[10]).addComponent(p[11])
								.addComponent(p[12]).addComponent(p[13]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(ln)
								.addComponent(pv[0]).addComponent(pv[1]).addComponent(pv[2])
								.addComponent(ln)
								.addComponent(pv[3])
								.addComponent(pv[4]).addComponent(pv[5])
								.addComponent(pv[6]).addComponent(pv[7])
								.addComponent(pv[8]).addComponent(pv[9])
								.addComponent(ln)
								.addComponent(pv[10]).addComponent(pv[11])
								.addComponent(pv[12]).addComponent(pv[13])));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(l[0]).addComponent(ln))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[0]).addComponent(pv[0]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[1]).addComponent(pv[1]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[2]).addComponent(pv[2]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(l[1]).addComponent(ln))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[3]).addComponent(pv[3]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[4]).addComponent(pv[4]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[5]).addComponent(pv[5]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[6]).addComponent(pv[6]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[7]).addComponent(pv[7]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[8]).addComponent(pv[8]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[9]).addComponent(pv[9]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(l[2]).addComponent(ln))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[10]).addComponent(pv[10]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[11]).addComponent(pv[11]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[12]).addComponent(pv[12]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[13]).addComponent(pv[13]))
				);
		return p0;
	}

	private boolean isFileOfInterest(String name) {
		name = name.toLowerCase();
		return name.contains("jogl") || name.contains("jocl") || name.contains("gluegen");
	}

	private JPanel getJOGLPanel() {
		JLabel p0 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib1.Name")  + " jogl.jar");
		JLabel p1 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib2.Name"));
		JLabel p2 = new JLabel("");
		
		String lib = "";
		for (int dir = 0; dir < 2; ++dir)
		{
			File folder = null;
			if (dir == 0)
			{
				folder = new File(Main.getProperty("extensionDirectory"));
			}
			else
			{
				PluginDescriptor imp3d = Main.getRegistry().getPluginDescriptor("de.grogra.imp3d");
				if (imp3d != null)
				{
					Object d = imp3d.getPluginDirectory();
					if (d instanceof File)
					{
						folder = (File) d;
						if (Main.usesProjectTree() && folder.getName().equals("build"))
						{
							folder = new File(folder.getParentFile(), "lib");
						}
					}
				}
			}
			if(folder!=null && folder.isDirectory()) {
				File[] listOfFiles = folder.listFiles();
				if(listOfFiles != null && listOfFiles.length>0) {
					for (File file : listOfFiles) {
						if(file.isFile()) {
							if(file.getName().toLowerCase().contains("jogl.jar")) {
								p0 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib0.Name") + " jogl.jar");
							}
							if(file.getName().toLowerCase().contains("jogl.") && file.getName().toLowerCase().contains(LIB_EXTENSIONS[getOSID()])) {
								if(file.getAbsolutePath().contains(javaVersion)) {
									p1 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib2.Name") + " : " +file.getName());
									lib = file.getAbsolutePath();
								}
							}
						} else {
							File[] tmp = file.listFiles();
							for (File file2 : tmp) {
								if(file2.isFile()) {
									if(file2.getName().toLowerCase().contains("jogl.") && file2.getName().toLowerCase().contains(LIB_EXTENSIONS[getOSID()])) {
										if(file2.getAbsolutePath().contains(javaVersion)) {
											p1 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib2.Name") + " : " +file2.getName());
											lib = file2.getAbsolutePath();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		logText(p0.getText());
		logText(p1.getText());

		JTextArea textArea = new JTextArea();
		textArea.setColumns(TEXT_WIDTH);
		textArea.setLineWrap(true);
		textArea.setRows(10);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setText(" ");
		
		if(lib.length()>0) {
			try {
				System.load(lib);
				p2 = new JLabel(logBundle("systeminfo.dilog.lib4.Name"));
				textArea.setText("("+lib+")");
			} catch (UnsatisfiedLinkError e) {
				p2 = new JLabel(logBundle("systeminfo.dilog.lib5.Name"));
				textArea.setText("("+lib+")\n"+logText(e.toString()));
			}
		}
		logText(textArea.getText());
		logText(System.getProperty("line.separator"));
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p.add(p0, c);
		c.gridx = 0;
		c.gridy = 2;
		p.add(p1, c);
		c.gridx = 0;
		c.gridy = 3;
		p.add(p2, c);
		c.gridx = 0;
		c.gridy = 4;
		p.add(new JScrollPane(textArea), c);
		c.gridx = 0;
		c.gridy = 5;
		p.add(Box.createVerticalStrut(250), c);
		return p;
	}

	private JPanel getJOCLLibPanel() {
		JLabel p0 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib1.Name")  + " jocl.jar");
		JLabel p1 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib2.Name"));
		JLabel p2 = new JLabel("");
		
		String lib = "";
		for (int dir = 0; dir < 2; ++dir)
		{
			File folder = null;
			if (dir == 0)
			{
				folder = new File(Main.getProperty("extensionDirectory"));
			}
			else
			{
				PluginDescriptor imp = Main.getRegistry().getPluginDescriptor("de.grogra.imp");
				if (imp != null)
				{
					Object d = imp.getPluginDirectory();
					if (d instanceof File)
					{
						folder = (File) d;
						if (Main.usesProjectTree() && folder.getName().equals("build"))
						{
							folder = new File(folder.getParentFile(), "lib");
						}
					}
				}
			}
			if(folder!=null && folder.isDirectory()) {
				File[] listOfFiles = folder.listFiles();
				if(listOfFiles != null && listOfFiles.length>0) {
					for (File file : listOfFiles) {
						if(file.isFile()) {
							if(file.getName().toLowerCase().contains("jocl.jar")) {
								p0 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib0.Name") + " jocl.jar");
							}
							if(file.getName().toLowerCase().contains("jocl") && file.getName().toLowerCase().contains("."+LIB_EXTENSIONS[getOSID()])) {
								if(file.getName().contains(javaVersion)) {
									p1 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib2.Name") + " : " +file.getName());
									lib = file.getAbsolutePath();
								}
							}
						} else {
							File[] tmp = file.listFiles();
							for (File file2 : tmp) {
								if(file2.isFile()) {
									if(file2.getName().toLowerCase().contains("jocl") && file2.getName().toLowerCase().contains("."+LIB_EXTENSIONS[getOSID()])) {
										if(file2.getName().contains(javaVersion)) {
											p1 = new JLabel(thisI18NBundle.getString("systeminfo.dilog.lib2.Name") + " : " +file2.getName());
											lib = file2.getAbsolutePath();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		logText(p0.getText());
		logText(p1.getText());

		JTextArea textArea = new JTextArea();
		textArea.setColumns(TEXT_WIDTH);
		textArea.setLineWrap(true);
		textArea.setRows(10);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);

		if(lib.length()>0) {
			try {
				System.load(lib);
				p2 = new JLabel(logBundle("systeminfo.dilog.lib4.Name"));
				textArea.setText("("+lib+")");
			} catch (UnsatisfiedLinkError e) {
				p2 = new JLabel(logBundle("systeminfo.dilog.lib5.Name"));
				textArea.setText("("+lib+")\n"+logText(e.toString()));
			}
		}
		logText(textArea.getText());
		logText(System.getProperty("line.separator"));

		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p.add(p0, c);
		c.gridx = 0;
		c.gridy = 2;
		p.add(p1, c);
		c.gridx = 0;
		c.gridy = 3;
		p.add(p2, c);
		c.gridx = 0;
		c.gridy = 4;
		p.add(new JScrollPane(textArea), c);

		return p;
	}

	private JPanel getJOCLPanel() {
		
		// Enable exceptions and subsequently omit error checks in this sample
		try {
			Class.forName("org.jocl.cl_device_id", false, this.getClass().getClassLoader());
			// it exists on the classpath
		} catch(ClassNotFoundException e) {
			// it does not exist on the classpath
			JPanel p2 = new JPanel();
			JTextArea ta = new JTextArea();
			ta.setText(logText(e.getMessage())+"\n JOCL libraries are missing.");
			ta.setForeground(Color.RED);
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(ta);
			scrollPane.setPreferredSize(new Dimension(520, 425));
			p2.add(scrollPane);
			return p2;
		}
		
		// Enable exceptions and subsequently omit error checks in this sample
		try {
			CL.setExceptionsEnabled(true);
		} catch (UnsatisfiedLinkError e) {
			JPanel p2 = new JPanel();
			JTextArea ta = new JTextArea();
			ta.setText(logText(e.getMessage()));
			ta.setForeground(Color.RED);
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(ta);
			scrollPane.setPreferredSize(new Dimension(520, 425));
			p2.add(scrollPane);
			return p2;
		}
		// Obtain the number of platforms
		int numPlatformsArray[] = new int[1];
		try {
			CL.clGetPlatformIDs(0, null, numPlatformsArray);
		} catch (org.jocl.CLException e) {
			JPanel p2 = new JPanel();
			JTextArea ta = new JTextArea();
			ta.setText(logText("Error: No CL platform found!\n"+e.getMessage()));
			ta.setForeground(Color.RED);
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setViewportView(ta);
			scrollPane.setPreferredSize(new Dimension(520, 425));
			p2.add(scrollPane);
			return p2;
		}
		
		// Obtain a platform ID
		cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
		CL.clGetPlatformIDs(platforms.length, platforms, null);

		JPanel p0 = new JPanel();
		p0.setLayout(new GridLayout(0,1));
		for(int platformIndex = 0; platformIndex<platforms.length; platformIndex++) {
			p0.add(getPlatformPanel(platforms[platformIndex]));
		}

		int max = 1;
		JLabel[] p = new JLabel[max];
		JLabel[] pv = new JLabel[max];
		p[0] = new JLabel(logBundle("systeminfo.dilog.jocl6.Name") + " :    ");
		p[0].setFont(defaultFont);
		pv[0] = new JLabel(logText(""+platforms.length));
		
		JPanel p1 = new JPanel();
		GroupLayout layout = new GroupLayout(p1);
		p1.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(p[0])
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(pv[0]))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[0]).addComponent(pv[0])));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		//c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p2.add(getJOCLLibPanel(), c);
		c.gridx = 0;
		c.gridy = 2;
		p2.add(p1, c);
		c.gridx = 0;
		c.gridy = 3;
		p2.add(new JScrollPane(p0), c);
		c.gridx = 0;
		c.gridy = 4;
		p2.add(Box.createVerticalStrut(10), c);
		return p2;
	}
	
	private JPanel getPlatformPanel(cl_platform_id platform) {
		final long deviceType = CL.CL_DEVICE_TYPE_ALL;

		// Initialize the context properties
		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		int numDevicesArray[] = new int[1];
		CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		int numDevices = numDevicesArray[0];

		// Obtain the device IDs
		cl_device_id devices[] = new cl_device_id[numDevices];
		CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);

		int max = 5;
		JLabel[] p = new JLabel[max];
		JLabel[] pv = new JLabel[max];

		p[0] = new JLabel(logBundle("systeminfo.dilog.jocl0.Name") + " : ");
		p[0].setFont(defaultFont);
		pv[0] = new JLabel(logText(getPlatformVersion(platform)));
		p[1] = new JLabel(logBundle("systeminfo.dilog.jocl1.Name") + " : ");
		p[1].setFont(defaultFont);
		pv[1] = new JLabel(logText(getPlatformName(platform)));
		p[2] = new JLabel(logBundle("systeminfo.dilog.jocl2.Name") + " : ");
		p[2].setFont(defaultFont);
		pv[2] = new JLabel(logText(getPlatformProfile(platform)));
		p[3] = new JLabel(logBundle("systeminfo.dilog.jocl3.Name") + " : ");
		p[3].setFont(defaultFont);
		pv[3] = new JLabel(logText(getVendor(platform)));
		logText("Extensions");
		logText(getExtensions(platform));
		p[4] = new JLabel(logBundle("systeminfo.dilog.jocl5.Name") + " : ");
		p[4].setFont(defaultFont);
		pv[4] = new JLabel(logText(""+numDevices));

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < numDevices; i++) {
			sb.append("Device " + i + " ("+deviceTypeToString(getDeviceType(devices[i]))+"): "+ getDeviceInfoString(devices[i], CL.CL_DEVICE_NAME)+System.getProperty("line.separator"));
		}

		JPanel p0 = new JPanel();
		GroupLayout layout = new GroupLayout(p0);
		p0.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(p[0]).addComponent(p[1]).addComponent(p[2]).addComponent(p[3]).addComponent(p[4]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(pv[0]).addComponent(pv[1]).addComponent(pv[2]).addComponent(pv[3]).addComponent(pv[4])));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[0]).addComponent(pv[0]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[1]).addComponent(pv[1]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[2]).addComponent(pv[2]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[3]).addComponent(pv[3]))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(p[4]).addComponent(pv[4])));

		JTextArea textArea = new JTextArea();
		textArea.setColumns(TEXT_WIDTH);
		textArea.setLineWrap(true);
		textArea.setRows(3);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		logText(System.getProperty("line.separator"));
		textArea.setText(logText(sb.toString()));

		JPanel p1 = new JPanel();
		p1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width
		c.fill = GridBagConstraints.HORIZONTAL;
		//top padding
		//c.insets = new Insets(3,3,3,3);
		
		c.gridx = 0;
		c.gridy = 1;
		p1.add(p0, c);
		c.gridx = 0;
		c.gridy = 2;
		p1.add(new JScrollPane(textArea), c);
		return p1;
	}

	
	public String deviceTypeToString( long type ) {
		if( type == CL.CL_DEVICE_TYPE_CPU )
			return "CPU";
		if( type == CL.CL_DEVICE_TYPE_GPU )
			return "GPU";
		if( type == CL.CL_DEVICE_TYPE_ACCELERATOR )
			return "ACCELERATOR";
		return "UNKNOWN";
	}
	
	public static long getDeviceType(cl_device_id device)
	{
		return getLong(device, CL.CL_DEVICE_TYPE );
	}
	
	private static String getPlatformVersion(cl_platform_id platform_id) {
		return getString(platform_id, CL.CL_PLATFORM_VERSION);
	}

	private static String getPlatformName(cl_platform_id platform_id) {
		return getString(platform_id, CL.CL_PLATFORM_NAME);
	}

	private static String getVendor(cl_platform_id platform_id) {
		return getString(platform_id, CL.CL_PLATFORM_VENDOR);
	}

	private static String getExtensions(cl_platform_id platform_id) {
		return getString(platform_id, CL.CL_PLATFORM_EXTENSIONS);
	}

	private static String getPlatformProfile(cl_platform_id platform_id) {
		return getString(platform_id, CL.CL_PLATFORM_PROFILE);
	}

	private static String getString(cl_platform_id platform, int paramName) {
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		CL.clGetPlatformInfo(platform, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int) size[0]];
		CL.clGetPlatformInfo(platform, paramName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length - 1);
	}

	/**
	 * Returns the value of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @return The value
	 */
	private static long getLong(cl_device_id device, int paramName)
	{
		return getLongs(device, paramName, 1)[0];
	}

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    private static long[] getLongs(cl_device_id device, int paramName, int numValues)
    {
        long values[] = new long[numValues];
        clGetDeviceInfo(device, paramName, Sizeof.cl_long * numValues, Pointer.to(values), null);
        return values;
    }

	/**
	 * Returns the value of the device info parameter with the given name
	 * 
	 * @param device The device
	 * @param paramName The parameter name
	 * @return The value
	 */
	private static String getDeviceInfoString(cl_device_id device, int paramName) {
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		CL.clGetDeviceInfo(device, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int) size[0]];
		CL.clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length - 1);
	}

	private int getOSID() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.indexOf("win") >= 0) return 0;
		if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) return 1;
		if(OS.indexOf("mac") >= 0) return 2;
		return 0;
	}

	
//as of Janurary 2019: There is no way anymore to get the List of loaded libraries.
//see: https://stackoverflow.com/a/55392604/2421048
	
public static class ClassScope {
        private static String[] EMPTY_LIBRARY_ARRAY = new String[]{""};
        private static final Throwable CVF_FAILURE; // set in <clinit>
 
        static {
                Throwable failure = null;
                Field tempf = null;
                try {
                        // this can fail if this is not a Sun-compatible JVM
                        // or if the security is too tight:
                        tempf = ClassLoader.class.getDeclaredField("loadedLibraryNames");
                        if (tempf.getType() != Vector.class) {
                                throw new RuntimeException("not of type java.util.Vector: " + tempf.getType().getName());
                        }
                        tempf.setAccessible(true);
                } catch (Throwable t) {
                        failure = t;
                }
                LIBRARIES_VECTOR_FIELD = tempf;
                CVF_FAILURE = failure;
 
                failure = null;
        }
 
        /**
         * Given a class loader instance, returns all native libraries currently loaded by
         * that class loader.
         *
         * @param defining
         *            class loader to inspect [may not be null]
         * @return Libraries loaded in this class loader [never null, may be empty]
         *
         * @throws RuntimeException
         *             if the "loadedLibraryNames" field hack is not possible in this JRE
         */
        @SuppressWarnings("unchecked")
        public static String[] getLoadedLibraries(final ClassLoader loader) {
                if (loader == null) {
                        throw new IllegalArgumentException("null input: loader");
                }
                if (LIBRARIES_VECTOR_FIELD == null) {
                        throw new RuntimeException("ClassScope::getLoadedLibraries() cannot be used in this JRE", CVF_FAILURE);
                }
 
                try {
                        final Vector<String> libraries = (Vector<String>) LIBRARIES_VECTOR_FIELD.get(loader);
                        if (libraries == null)
                                return EMPTY_LIBRARY_ARRAY;
 
                        final String[] result;
 
                        // note: Vector is synchronized in Java 2, which helps us make
                        // the following into a safe critical section:
                        synchronized (libraries) {
                                result = libraries.toArray(new String[] {});
                        }
                        return result;
                }
                // this should not happen if <clinit> was successful:
                catch (IllegalAccessException e) {
                        //e.printStackTrace(System.out);
                        EMPTY_LIBRARY_ARRAY[0] = "Not possible to get the List of loaded libraries for jdk9+ ondwards!";
                        System.out.println(EMPTY_LIBRARY_ARRAY[0]);
                        return EMPTY_LIBRARY_ARRAY;
                }
        }
 
        /**
         * A convenience multi-loader version of
         * {@link #getLoadedLibraries(ClassLoader)}.
         *
         * @param an
         *            array of defining class loaders to inspect [may not be null]
         * @return String array [never null, may be empty]
         *
         * @throws RuntimeException
         *             if the "loadedLibrayNames" field hack is not possible in this JRE
         */
        public static String[] getLoadedLibraries(final ClassLoader[] loaders) {
                if (loaders == null) {
                        throw new IllegalArgumentException("null input: loaders");
                }
                final List<String> resultList = new LinkedList<String>();
 
                for (int l = 0; l < loaders.length; ++l) {
                        final ClassLoader loader = loaders[l];
                        if (loader != null) {
                                final String[] libraries = getLoadedLibraries(loaders[l]);
                                resultList.addAll(Arrays.asList(libraries));
                        }
                }
 
                final String[] result = new String[resultList.size()];
                resultList.toArray(result);
 
                return result;
        }
 
        private ClassScope() {
        } // this class is not extendible
 
        private static final Field LIBRARIES_VECTOR_FIELD; // set in <clinit> [can be
        // null]
 
}
}

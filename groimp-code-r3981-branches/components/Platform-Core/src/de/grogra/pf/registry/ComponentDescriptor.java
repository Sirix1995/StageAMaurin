/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.pf.registry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import de.grogra.pf.boot.Main;
import de.grogra.util.I18NBundle;
import de.grogra.util.WrapException;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;

public final class ComponentDescriptor extends Item {
	
	private static final long serialVersionUID = 1234645797654L;

	private static int SPACING_HORIZONTAL_MINOR	= 10;
	private static int SPACING_VERTICAL_MINOR	= 10;

	private static int SPACING_HORIZONTAL_END	= 100;

	private static final Color tableHeaderBackground = new Color(204,204,255);
	private static final Color tableBorder = new Color(178,178,178);

	private ComponentLibraryClassLoader loader;
	private FileSystem fileSystem;
	private Object directory;

	private I18NBundle i18n = null;

	private final String version = "";
	private final String author = "";
	private final String componentID = "";
	private String moduleName = "";
	private String directoryName = "";
	private String fileName = "";
	private JarFile file = null;

	ArrayList<ComponentDescriptionContent> descriptionContent;

	/**
	 * 	Identification key under which this component descriptor is stored at the registry.
	 * 
	 * @return
	 */
	public String getIdentificationKey ()
	{
		return directoryName.substring (directoryName.indexOf ("componentLibrary")+16, directoryName.length ());
	}

	public String getVersion ()
	{
		return version;
	}

	public String getAuthor ()
	{
		return author;
	}

	public String getComponentID () {
		return componentID;
	}


	ComponentDescriptor () {
		super (null);
		descriptionContent = new ArrayList<ComponentDescriptionContent>();
	}

	public static ComponentDescriptor createCoreDescriptor (File libDir) {
		ComponentDescriptor cd = new ComponentDescriptor ();
		cd.setName ("components");
		cd.setI18NBundle (Main.getI18NBundle ());
		cd.fileSystem = LocalFileSystem.FILE_ADAPTER;
		return cd;
	}

	public static ComponentDescriptor createCoreDescriptorDir(String name) {
		ComponentDescriptor cd = new ComponentDescriptor ();
		cd.setName (name);
		return cd;
	}

	@Override
	public Object get (Object key, Object defaultValue) {
		return "";
	}

	@Override
	protected boolean readAttribute (String uri, String name, String value) throws org.xml.sax.SAXException {
		return true;
	}

	/**
	 * 
	 * 
	 * @param file
	 * @param fs
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static ComponentDescriptor read (File file, FileSystem fs, Object dir)
			throws IOException
			{
		ComponentDescriptor pd = null;

		//get description XML file from component archive file
		JarFile jarFile = new JarFile(file);
		ZipEntry entry = jarFile.getEntry("description.txt");

		//get input stream to description XML file
		InputStream descripInputStream = jarFile.getInputStream(entry);

		//If input stream is valid, load XML contents into array and set to componentDescriptor instance
		if(descripInputStream != null)
		{
			ArrayList<ComponentDescriptionContent> descripContent = load(descripInputStream);
			//			jarFile.close(); //yong 1 Sept 2012 - may have problem closing this and not triggering event?

			if(descripContent != null)
			{
				pd = new ComponentDescriptor();
				pd.setDescriptionContent(descripContent);
				pd.setName(file.getName ());
				pd.setFile(jarFile);
				pd.fileSystem = fs;
				pd.directory = dir;
				pd.directoryName = file.getAbsolutePath();
			}
		}

		//return componentDescriptor instance (can be null)
		return pd;
	}

	public static ArrayList<ComponentDescriptionContent> load (InputStream is)
	{
		try
		{
			return ComponentDescriptionXMLReader.readXML(is);
		}
		catch (Throwable t)
		{
			System.out.println ("Error in LoadDescription");
			return null;
		}
		finally
		{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getFileName() {
		return fileName;
	}

	public Enumeration<JarEntry> getEntrys() {
		return file.entries ();
	}

	public Vector<String> getRggFileVector() {
		Enumeration<JarEntry> list = getEntrys();
		Vector<String>rggFileList = new Vector<String>();
		while (list.hasMoreElements()) {
			JarEntry entry = list.nextElement();
			if(entry.getName ().toLowerCase ().contains (".rgg")) rggFileList.add (entry.getName ());
		}		
		return rggFileList;
	}

	public String[] getRggFileList() {
		Vector<String> v = getRggFileVector();
		return v.toArray(new String[v.size()]);
	}

	public InputStream getRggInputStream(String fileName) throws IOException {
		Vector<String> fileVector = getRggFileVector();
		if(!fileVector.contains (fileName)) return null;
		return file.getInputStream(file.getJarEntry(fileName));		
	}


	private void setFileName(String name) {
		fileName = name;
	}


	public JarFile getFile() {
		return file;
	}

	private void setFile(JarFile file) {
		this.file = file;
		setFileName(file.getName ());		
	}

	public Object getComponentDirectory ()
	{
		return directory;
	}


	public FileSystem getFileSystem ()
	{
		return fileSystem;
	}

	@Override
	public String toString ()
	{
		return "Component[" + getFileName () + "]"; 
	}


	@Override
	public I18NBundle getI18NBundle ()
	{
		return i18n;
	}


	public void setI18NBundle (I18NBundle bundle)
	{
		this.i18n = bundle;
	}

	@Override
	public ClassLoader getClassLoader () {
		return loader;
	}


	public ComponentClassLoader getComponentClassLoader () {
		return loader;
	}


	public URL getURLForResource (String name) {
		try {
			return new URL ("componentLibrary", null, -1, getName () + '/' + name);
		} catch (MalformedURLException e) {
			throw new WrapException (e);
		}
	}


	public static ComponentDescriptor getInstance (String key) {
		return Main.getRegistry ().getComponent (key);
	}


	public static ComponentDescriptor getInstance (Class cls) {
		return getInstance (cls.getClassLoader ());
	}


	public static ComponentDescriptor getInstance (ClassLoader loader) {
		while (loader != null) {
			if (loader instanceof ComponentClassLoader) {
				return ((ComponentClassLoader) loader).descriptor;
			}
			loader = loader.getParent ();
		}
		return null;
	}


	public File getConfigurationDirectory () {
		File f = Main.getConfigurationDirectory ();
		if (f == null) {
			return null;
		}
		f = new File (f, getName ());
		return (f.isDirectory () || f.mkdir ()) ? f : null;
	}


	public JScrollPane getDescriptionPanel() {	
		return (JScrollPane)(ComponentDescriptor.getDescriptionPanel(descriptionContent));
	}


	public void setDescriptionContent(ArrayList<ComponentDescriptionContent> contentList)
	{
		this.descriptionContent = contentList;
	}

	public ArrayList<ComponentDescriptionContent> getDescriptionContent()
	{
		return descriptionContent;
	}

	private static void drawSeparator(JPanel panel)
	{
		drawSpace(panel,15);
		panel.add(new JSeparator(SwingConstants.HORIZONTAL));
		drawSpace(panel,15);
	}

	private static void drawSpace(JPanel panel, int size)
	{
		panel.add(Box.createRigidArea(new Dimension(size,size)));
	}

	/**
	 * Top panel - For displaying module's main description
	 * Possible information - Package, inheritance hierarchy, interface implementations
	 *                      - Tagged information
	 * @param content
	 * @param fonts
	 * @return
	 */
	private static JPanel drawPanelTop(ComponentDescriptionContent content,ComponentDescriptionFont fonts)
	{		
		//create panel
		JPanel panelTop = new JPanel();
		ComponentDescriptionGridLayout layoutPanelTop = new ComponentDescriptionGridLayout(2,1);
		panelTop.setLayout(layoutPanelTop);

		//create sub panel
		JPanel panelTopSub = new JPanel();
		panelTopSub.setBackground(Color.WHITE);
		ComponentDescriptionGridLayout layoutPanelTopSub = new ComponentDescriptionGridLayout(0,7);
		layoutPanelTopSub.setHgap(0);
		panelTopSub.setLayout(layoutPanelTopSub);

		//Main panel - Module name
		JLabel label = new JLabel("Module " + content.getName());
		label.setFont(fonts.getFontHeader1());
		panelTop.add(label);

		//Sub panel - empty line
		JLabel empty3 = new JLabel(" ");
		JLabel empty4 = new JLabel(" ");
		JLabel empty5 = new JLabel(" ");
		panelTopSub.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panelTopSub.add(empty3);
		panelTopSub.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panelTopSub.add(empty4);
		panelTopSub.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panelTopSub.add(empty5);
		panelTopSub.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_END));

		//add sub panel to main panel
		panelTop.add(panelTopSub);

		//return main panel
		return panelTop;
	}

	private static void drawPanelTopDescriptionInfo(JPanel panel, String label, String content,ComponentDescriptionFont fonts)
	{
		JLabel labelDate = new JLabel(label);
		JLabel colon1 = new JLabel(":");
		JLabel contentDate = new JLabel(content);

		// gets fonts for normal text
		Font fontNormal = fonts.getFontNormal();
		Font fontHeader3 = fonts.getFontHeader3();

		labelDate.setFont(fontHeader3);
		contentDate.setFont(fontNormal);

		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(labelDate);
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(colon1);
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_END));

		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(contentDate);
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_MINOR));
		panel.add(Box.createHorizontalStrut(SPACING_HORIZONTAL_END));
	}

	private static JPanel drawPanelTopDescription(ComponentDescriptionContent content,ComponentDescriptionFont fonts)
	{
		//Create panel
		JPanel panelTopDescrip = new JPanel();		
		ComponentDescriptionGridLayout layoutPanelTopDescrip = new ComponentDescriptionGridLayout(5,1);
		panelTopDescrip.setLayout(layoutPanelTopDescrip);

		//create sub panel
		JPanel panelTopDescripSub = new JPanel();
		panelTopDescripSub.setBackground(Color.WHITE);
		ComponentDescriptionGridLayout layoutpanelTopDescripSub = new ComponentDescriptionGridLayout(0,7);
		layoutpanelTopDescripSub.setHgap(0);
		panelTopDescripSub.setLayout(layoutpanelTopDescripSub);

		//create sub panel indent container
		JPanel panelTopDescripSubIndent = new JPanel();
		ComponentDescriptionGridLayout layoutTopDescripSubIndent = new ComponentDescriptionGridLayout(1,2);
		layoutTopDescripSubIndent.setHgap(0);
		panelTopDescripSubIndent.setLayout(layoutTopDescripSubIndent);

		// gets fonts for  text
		Font fontNormal = fonts.getFontNormal();

		//Main panel - Module name
		JLabel label = new JLabel(/*SPACING + */content.getName());
		label.setFont(fonts.getFontCodeNormal());
		panelTopDescrip.add(label);

		drawSpace(panelTopDescrip,15);

		//Main panel - Description
		JLabel contentDescp = new JLabel(/*SPACING + */content.getDescription(0));
		contentDescp.setFont(fontNormal);
		if(content.getDescriptionCount()>0)
			panelTopDescrip.add(contentDescp);
		else
		{
			JLabel empty = new JLabel("");
			panelTopDescrip.add(empty);
		}

		drawSpace(panelTopDescrip,15);

		//Sub panel - Date
		drawPanelTopDescriptionInfo(panelTopDescripSub, ComponentDescriptionContent.XML_TAG_DATE,content.getDate(),fonts);

		//Sub panel - Author
		drawPanelTopDescriptionInfo(panelTopDescripSub, ComponentDescriptionContent.XML_TAG_AUTHOR,content.getAuthor(),fonts);

		//Sub panel - Version
		drawPanelTopDescriptionInfo(panelTopDescripSub, ComponentDescriptionContent.XML_TAG_VERSION,content.getVersion(),fonts);

		//add sub panel to indent panel
		panelTopDescripSubIndent.add(panelTopDescripSub);

		//add indent panel to main panel
		panelTopDescrip.add(panelTopDescripSubIndent);

		return panelTopDescrip;
	}

	private static JPanel drawPanelDetailsInternal(ComponentDescriptionContent content, ComponentDescriptionFont fonts)
	{
		//Create panel
		JPanel panelTopDescrip = new JPanel();		
		ComponentDescriptionGridLayout layoutPanelTopDescrip = new ComponentDescriptionGridLayout(5,1);
		panelTopDescrip.setLayout(layoutPanelTopDescrip);
		panelTopDescrip.setBackground(Color.WHITE);

		//create sub panel
		JPanel panelTopDescripSub = new JPanel();
		panelTopDescripSub.setBackground(Color.WHITE);
		ComponentDescriptionGridLayout layoutpanelTopDescripSub = new ComponentDescriptionGridLayout(0,7);
		layoutpanelTopDescripSub.setHgap(0);
		panelTopDescripSub.setLayout(layoutpanelTopDescripSub);

		//create sub panel indent container
		JPanel panelTopDescripSubIndent = new JPanel();
		ComponentDescriptionGridLayout layoutTopDescripSubIndent = new ComponentDescriptionGridLayout(1,2);
		layoutTopDescripSubIndent.setHgap(0);
		panelTopDescripSubIndent.setLayout(layoutTopDescripSubIndent);

		// gets fonts for  text
		Font fontNormal = fonts.getFontNormal();
		Font fontHeader2 = fonts.getFontHeader2();

		//Main panel - method,constructor or variable name
		JLabel label = new JLabel(/*SPACING + */content.getName());
		label.setFont(fontHeader2);
		panelTopDescrip.add(label);

		drawSpace(panelTopDescrip,15);

		//Main panel - Description
		String descrip = content.getDescription(0);
		
		JTextArea textArea = new JTextArea(descrip.trim());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(fontNormal);
		textArea.setEditable(false);
		textArea.setTabSize(0);
		
		if(content.getDescriptionCount()>0)
		{
			panelTopDescrip.add(textArea);
		}
		else
		{
			JLabel empty = new JLabel("");
			panelTopDescrip.add(empty);
		}

		drawSpace(panelTopDescrip,15);

		//Sub panel - Date
		if(!content.getDate().equals(""))
			drawPanelTopDescriptionInfo(panelTopDescripSub, ComponentDescriptionContent.XML_TAG_DATE,content.getDate(),fonts);

		//Sub panel - Author
		if(!content.getAuthor().equals(""))
			drawPanelTopDescriptionInfo(panelTopDescripSub, ComponentDescriptionContent.XML_TAG_AUTHOR,content.getAuthor(),fonts);

		//Sub panel - Version
		if(!content.getVersion().equals(""))
			drawPanelTopDescriptionInfo(panelTopDescripSub, ComponentDescriptionContent.XML_TAG_VERSION,content.getVersion(),fonts);

		//add sub panel to indent panel
		panelTopDescripSubIndent.add(panelTopDescripSub);

		//add indent panel to main panel
		panelTopDescrip.add(panelTopDescripSubIndent);

		return panelTopDescrip;
	}

	private static JPanel drawPanelDetails(ArrayList<ComponentDescriptionContent> contents,ComponentDescriptionFont fonts, String header)
	{
		if(contents==null)
			return null;

		if(contents.size()==0)
			return null;

		//Create container panel
		JPanel panelContainer = new JPanel();		
		ComponentDescriptionGridLayout layoutPanelContainer = new ComponentDescriptionGridLayout(0,1);
		panelContainer.setLayout(layoutPanelContainer);
		panelContainer.setBackground(Color.WHITE);

		//Get fonts
		Font fontHeader1 = fonts.getFontHeader1();

		//Create header
		JLabel labelHead = new JLabel(header);
		labelHead.setFont(fontHeader1);
		labelHead.setBorder(BorderFactory.createLineBorder(tableBorder,2));
		labelHead.setBackground(tableHeaderBackground);
		labelHead.setOpaque(true);

		//Add header
		panelContainer.add(labelHead);

		//Add spacing
		drawSpace(panelContainer,SPACING_VERTICAL_MINOR);

		for(int i=0; i<contents.size(); ++i)
		{
			ComponentDescriptionContent content = contents.get(i);

			JPanel internalPanel = drawPanelDetailsInternal(content,fonts);
			if(internalPanel!=null)
				panelContainer.add(internalPanel);

			//separator between each method, variable or constructor
			if(i!=contents.size()-1) //no separator after last entry
				drawSeparator(panelContainer);
		}

		return panelContainer;
	}


	private static JPanel drawPanelMethodSummary(ArrayList<ComponentDescriptionContent> contents,ComponentDescriptionFont fonts)
	{
		if(contents==null)
			return null;

		if(contents.size()==0)
			return null;

		//Create panel
		JPanel panelContainer = new JPanel();		
		ComponentDescriptionGridLayout layoutPanelContainer = new ComponentDescriptionGridLayout(2,1);
		panelContainer.setLayout(layoutPanelContainer);
		panelContainer.setBorder(BorderFactory.createLineBorder(tableBorder,1));

		//Create panel and layout and layout constraints
		JPanel panel = new JPanel();
		ComponentDescriptionGridLayout layoutPanel = new ComponentDescriptionGridLayout(contents.size(),2);
		panel.setLayout(layoutPanel);
		
		//Get fonts
		Font fontHeader1 = fonts.getFontHeader1();
		Font fontCodeNorm = fonts.getFontCodeNormal();

		//Create header
		JLabel labelHead = new JLabel("Method Summary");
		labelHead.setFont(fontHeader1);
		labelHead.setBorder(BorderFactory.createLineBorder(tableBorder,1));
		labelHead.setBackground(tableHeaderBackground);
		labelHead.setOpaque(true);

		//Add header
		panelContainer.add(labelHead);

		for(int i=0; i<contents.size(); ++i)
		{
			ComponentDescriptionContent content = contents.get(i);

			//Left column - method specifiers
			int specCount = content.getSpecifierCount();
			String specs = "";
			for(int j=0; j<specCount; ++j)
				specs += (content.getSpecifier(j)+" ");

			JLabel label = new JLabel(specs);
			label.setFont(fontCodeNorm);
			label.setBorder(BorderFactory.createLineBorder(tableBorder,1));
			panel.add(label);

			//Right column - method name and signature
			String name = content.getName();
			int paramCount = content.getParamCount();
			if(paramCount > 0)
				name += " (";
			for(int k=0; k<paramCount; ++k)
				name += (" " + content.getParam(k));
			if(paramCount > 0)
				name += " )"; 
			JLabel labelR = new JLabel(name);
			labelR.setFont(fontCodeNorm);
			labelR.setBorder(BorderFactory.createLineBorder(tableBorder,1));
			panel.add(labelR);
		}

		//return panel;
		panelContainer.add(panel);
		return panelContainer;
	}

	/**
	 * Main method for drawing Component Description Window.
	 * panelMain contains all the sub-panels.
	 * sub-panels, in order of appearance from top to bottom are:
	 *   panelTop
	 *   panelTopDescrip
	 *   panelMethodSummary
	 *   panelMethodDetails
	 * @param contents
	 * @return
	 */
	public static Object getDescriptionPanel(ArrayList<ComponentDescriptionContent> contents)
	{

		// Indent
		JPanel panelIndent= new JPanel();
		ComponentDescriptionGridLayout layoutIndent= new ComponentDescriptionGridLayout(1,2);
		panelIndent.setLayout(layoutIndent);
		panelIndent.add(Box.createRigidArea(new Dimension(SPACING_HORIZONTAL_MINOR,SPACING_VERTICAL_MINOR)));

		// Main layout
		JPanel panelMain = new JPanel();
		ComponentDescriptionGridLayout layoutMain = new ComponentDescriptionGridLayout(0,1);
		panelMain.setLayout(layoutMain);

		// Sub layouts - in order of appearance from top to bottom
		JPanel panelTop = null;
		JPanel panelTopDescrip = null;
		JPanel panelMethodSummary = null;
		JPanel panelMethodDetails = null;

		// Create fonts
		ComponentDescriptionFont fonts = new ComponentDescriptionFont();

		//		public static final String XML_TAG_MODULE 					= "module";
		//				public static final String XML_TAG_MODULE_SPECIFIERS	 	= "moduleSpecifiers";
		//				public static final String XML_TAG_MODULE_SPECIFIER_ELEMENT	= "moduleSpecifierElement";
		//				public static final String XML_TAG_MODULE_PARAMETERS	 	= "moduleParameters";
		//				public static final String XML_TAG_MODULE_PARAMETER_ELEMENT	= "moduleParameterElement";
		//				
		//				public static final String XML_TAG_CONTENT 					= "content";
		//				
		//				public static final String XML_TAG_NAME 			= "name";
		//				public static final String XML_TAG_DATE 			= "date";
		//				public static final String XML_TAG_AUTHOR 			= "author";
		//				public static final String XML_TAG_DESCRIPTION 		= "description";
		//				public static final String XML_TAG_VERSION			= "version";
		//				public static final String XML_TAG_PARAMETER		= "parameter";
		//				public static final String XML_TAG_SPECIFIER		= "specifier";
		//				public static final String XML_TAG_RETURN			= "return";

		String moduleName = "";
		ArrayList<ComponentDescriptionContent> methods = new ArrayList<ComponentDescriptionContent>();
		ArrayList<ComponentDescriptionContent> constructors = new ArrayList<ComponentDescriptionContent>();
		ArrayList<ComponentDescriptionContent> variables = new ArrayList<ComponentDescriptionContent>();

		for(int i=0; i<contents.size(); ++i)
		{
			ComponentDescriptionContent content = contents.get(i);

			//check if this content is module descriptor
			if(content.getSpecifierCount()>0)
			{
				if(content.getSpecifier(0).equalsIgnoreCase(ComponentDescriptionContent.XML_TAG_MODULE))
				{
					//create top panel - to show module name, as well as it's hierarchical identity
					panelTop = drawPanelTop(content,fonts);
					//create description - details of this module
					panelTopDescrip = drawPanelTopDescription(content,fonts);
					//set variable to remember name of this module
					moduleName = content.getName();

					continue;
				}
			}

			//check if this content is module constructor
			if(content.getName().equals(moduleName))
			{
				if(content.isMethod())
				{
					//add to list of constructors
					constructors.add(content);
					continue;
				}

			}

			//check if this content is method
			if(content.isMethod())
			{
				methods.add(content);
			}
			//check if this content is variable
			else
			{
				variables.add(content);
			}
		}

		//draw method summary
		panelMethodSummary = drawPanelMethodSummary(methods,fonts);
		
		//draw method details
		panelMethodDetails = drawPanelDetails(methods,fonts, "Method Detail");

		//add in sub panels to main panel
		if(panelTop!=null)
		{
			drawSeparator(panelMain);
			panelTop.setBackground(Color.WHITE);
			panelMain.add(panelTop);					//Class hierarchy
		}
		if(panelTopDescrip!=null)
		{
			drawSeparator(panelMain);
			panelTopDescrip.setBackground(Color.WHITE);
			panelMain.add(panelTopDescrip);				//Class description
		}
		//(panelMain);
		//panelMain.add(panelCentre);					//Field Summary
		//(panelMain);
		//.add(panelCentre);							//Constructor Summary
		if(panelMethodSummary!=null)
		{
			drawSeparator(panelMain);
			panelMethodSummary.setBackground(Color.WHITE);
			panelMain.add(panelMethodSummary);			//Method Summary
		}
		//drawSeparator(panelMain);
		//panelMain.add(panelCentre);					//Field Details
		//drawSeparator(panelMain);
		//panelMain.add(panelCentre);					//Constructor Details
		if(panelMethodDetails!=null)
		{
			drawSeparator(panelMain);
			panelMain.add(panelMethodDetails);			//Method Details
		}
		//panelMain.add(Box.createVerticalStrut(SPACING_VERTICAL_END));

		//Add final separator and spacing at the end
		drawSeparator(panelMain);

		//Set all background to white
		panelMain.setBackground(Color.WHITE);
		panelIndent.setBackground(Color.WHITE);

		//add main panel to indentation panel
		panelIndent.add(panelMain);

		//set preferred width of panelMain. necessary for line wrapping
		panelMain.setPreferredSize(new Dimension(200,panelMain.getPreferredSize().height+methods.size()*50)); 
		//extra 50 pixels for every method description line to cater for wrapping
		
		//Allow scrolling when window size does not fit contents
		JScrollPane scrollPane = new JScrollPane(panelIndent);
		
		return scrollPane;
		//((Panel)panel).setContent(new ComponentWrapperImpl( scrollPane,null));
	}
	
	public void setModuleName (String value) {
		moduleName = value;
	}
	
	public String getModuleName()
	{
		return moduleName;
	}
	
	public String loadModuleName()
	{
		for(int i=0; i<descriptionContent.size(); ++i)
		{
			ComponentDescriptionContent content = descriptionContent.get(i);

			//check if this content is module descriptor
			if(content.getSpecifierCount()>0)
			{
				if(content.getSpecifier(0).equalsIgnoreCase(ComponentDescriptionContent.XML_TAG_MODULE))
				{
					moduleName = content.getName();
					return content.getName();
				}
			}
		}
		return "";
	}
	
	public ArrayList<String> getMethodNames()
	{
		ArrayList<String> methods = new ArrayList<String>();
		for(int i=0; i<descriptionContent.size(); ++i)
		{
			ComponentDescriptionContent content = descriptionContent.get(i);

			if((content.isMethod()) && (!content.getName().equals(moduleName)) )
			{
				methods.add(content.getName());
			}
		}
		return methods;
	}
	
	public ArrayList<String> getInSlots()
	{
		ArrayList<String> inSlots = new ArrayList<String>();
		for(int i=0; i<descriptionContent.size(); ++i)
		{
			ComponentDescriptionContent content = descriptionContent.get(i);

			if((content.isMethod()) && (!content.getName().equals(moduleName)) )
			{
				if(content.getName().equals("setSlots"))
				{
					int inSlotCount = content.getInSlotCount();
					
					for(int j=0; j< inSlotCount; j++)
					{
						inSlots.add(content.getInSlot(j));
					}
				}
			}
		}
		return inSlots;
	}
	
	public ArrayList<String> getOutSlots()
	{
		ArrayList<String> outSlots = new ArrayList<String>();
		for(int i=0; i<descriptionContent.size(); ++i)
		{
			ComponentDescriptionContent content = descriptionContent.get(i);

			if((content.isMethod()) && (!content.getName().equals(moduleName)) )
			{
				int inSlotCount = content.getOutSlotCount();
				
				for(int j=0; j< inSlotCount; j++)
				{
					outSlots.add(content.getOutSlot(j));
				}
			}
		}
		return outSlots;
	}
}

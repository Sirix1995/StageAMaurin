package de.grogra.bwinReader;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.swing.*;
public class SettingGui extends PanelSupport {
	public Registry r;
	JLabel coniferData,deciduousData;
	JTextArea coniferInp,deciduousInp;
	JRadioButton simpel,advanced,no,basic;
	private DefaultListModel<ParameterValue> selectModel;
	private DefaultListModel<String> activFilter;
	final ParameterHandler ph;
	public SettingGui(Workbench workbench) {
		super(new SwingPanel(null));
		ph=new ParameterHandler();
		selectModel=new DefaultListModel<ParameterValue>();
		activFilter=new DefaultListModel<String>();
		for(ParameterValue p:ph.getSelection()) {
			selectModel.addElement(p);			
		}
		for(FilterItem fi:ph.getFilters()) {
			activFilter.addElement(fi.printReal());
		}
		//generate Gui
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//Basic Settings ========================================================
		JPanel basicSetting =new JPanel();
		basicSetting.setLayout(new BorderLayout());
		//radioButton
		JPanel radioButtonBox =new JPanel();
		JLabel info=new JLabel("TreePointer Settings");
		radioButtonBox.add(info);
		no =new JRadioButton("No");
		basic=new JRadioButton("basic");
		simpel =new JRadioButton("Simple");
		advanced=new JRadioButton("Advanced");
		final ButtonGroup gruppe = new ButtonGroup();
		gruppe.add(no);
		gruppe.add(basic);
		gruppe.add(simpel);
		gruppe.add(advanced);
		radioButtonBox.add(no);
		radioButtonBox.add(basic);
		radioButtonBox.add(simpel);
		radioButtonBox.add(advanced);
		basicSetting.add(radioButtonBox,BorderLayout.NORTH);

		JPanel listBox =new JPanel();
		listBox.setLayout(new BorderLayout());
		JPanel listBox_listen=new JPanel();
		listBox_listen.setLayout(new GridLayout(1,2));
		JScrollPane scrollPane_all = new JScrollPane();
		final JList<String> all =new JList(ph.getListe());
		scrollPane_all.setViewportView(all);
		listBox_listen.add(scrollPane_all);
		
		JScrollPane scrollPane_selection = new JScrollPane();
		final JList selection =new JList(selectModel);
		scrollPane_selection.setViewportView(selection);
		listBox_listen.add(scrollPane_selection);
		
		JPanel listBox_button =new JPanel();
		JButton add2List =new JButton("add");
		listBox_button.add(add2List);
		JButton removeFromList =new JButton("drop");
		listBox_button.add(removeFromList);
		
		
		listBox.add(listBox_listen,BorderLayout.CENTER);
		listBox.add(listBox_button,BorderLayout.SOUTH);
		basicSetting.add(listBox,BorderLayout.CENTER);
		
		add2List.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int[] val =all.getSelectedIndices();
				ph.addSelection(val);
				 updateSelectList();				
			}
		});

		removeFromList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ph.dropSelection(selection.getSelectedIndex());
				updateSelectList();
			}
		});
				//baseModel===========================
		JPanel baseModel =new JPanel();
		baseModel.setLayout(new GridLayout(4,1));
		coniferData=new JLabel("conifer"+ph.selectVariableList());
		coniferInp= new JTextArea(ph.getConiferModel());
		deciduousData=new JLabel("deciduous"+ph.selectVariableList());
		deciduousInp=new JTextArea(ph.getDeciduousModel());
		baseModel.add(coniferData);
		baseModel.add(coniferInp);
		baseModel.add(deciduousData);
		baseModel.add(deciduousInp);
		//Filter===========================
		JPanel filterSettings=new JPanel();
		filterSettings.setLayout(new GridLayout(3,1));
		JScrollPane scrollFilter = new JScrollPane();
		final JList filters=new JList(activFilter);
		scrollFilter.setViewportView(filters);
		filterSettings.add(scrollFilter);
		
		JPanel filterSettings_addBar =new JPanel();
		final JComboBox elements=new JComboBox(ph.getListe());
		filterSettings_addBar.add(elements);
		String[] operators= {"==","<",">","!="};
		final JComboBox<String> ope=new JComboBox<String>(operators);
		filterSettings_addBar.add(ope);
		filterSettings.add(filterSettings_addBar);
		final JTextField val=new JTextField(10);
		filterSettings_addBar.add(val);
		JButton addFilter =new JButton("add");
		filterSettings_addBar.add(addFilter);
		addFilter.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.out.println(elements.getSelectedIndex());
				FilterItem f=new FilterItem(elements.getSelectedIndex(),ph.getListe()[elements.getSelectedIndex()],val.getText(),ope.getSelectedIndex());
				ph.addFilter(f);
				System.out.println(f);
				updateFilterList();
			}
		});
		JPanel filterSettings_editBar=new JPanel();	
		JButton dropFilter=new JButton("remove");
		JButton clearFilter=new JButton("remove All");
		filterSettings_editBar.add(dropFilter);
		filterSettings_editBar.add(clearFilter);
		filterSettings.add(filterSettings_editBar);
		dropFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ph.removeFilter(filters.getSelectedIndex());
				updateFilterList();
			}
		});
		clearFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ph.removeAllFilter();
				updateFilterList();
			}
		});
		
		//bring all together===============
		JTabbedPane settings=new JTabbedPane();
		settings.addTab("basic",basicSetting);
		settings.addTab("model",baseModel);
		settings.addTab("filter", filterSettings);
		panel.add(settings,BorderLayout.NORTH);
		JPanel buttonBar=new JPanel();
		
		JButton save=new JButton("save");
		JButton reset =new JButton("reset");
		
		buttonBar.add(save);
		buttonBar.add(reset);
		panel.add(buttonBar,BorderLayout.SOUTH);
		//add Gui to groIMP
		((SwingPanel) getComponent()).getContentPane().add(panel);
		
		//inerst old settings
		updateSelectList();
		save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int mode=0;
				if(basic.isSelected()) {mode=1;}
				if(simpel.isSelected()) {mode=2;}
				if(advanced.isSelected()) {mode=3;}
				ph.setPointerMode(mode);
				ph.setConiferModel(coniferInp.getText());
				ph.setDeciduousModel(deciduousInp.getText());
				ph.saveData();
				
			}
			
		});
		reset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				ph.reset();
				updateSelectList();
				updateFilterList();
				coniferInp.setText(ph.getConiferModel());
				deciduousInp.setText(ph.getDeciduousModel());
			}
		});

	}
	private void updateSelectList(){
		selectModel.removeAllElements();
		for(ParameterValue p:ph.getSelection()) {
			selectModel.addElement(p);
		}
		int radioButt=ph.getPointerMode();
		if(radioButt==1) {
			basic.setSelected(true);
			simpel.setSelected(false);
			advanced.setSelected(false);
			no.setSelected(false);
		}else if(radioButt==2){
			basic.setSelected(false);
			simpel.setSelected(true);
			advanced.setSelected(false);
			no.setSelected(false);
		}else if(radioButt==3){
			basic.setSelected(false);
			simpel.setSelected(false);
			advanced.setSelected(true);
			no.setSelected(false);
		}else if(radioButt==0){
			basic.setSelected(false);
			simpel.setSelected(true);
			advanced.setSelected(false);
			no.setSelected(true);
		}
		coniferData.setText("Conifer"+ph.selectVariableList());
		deciduousData.setText("Deciduous"+ph.selectVariableList());
	}
	private void updateFilterList() {
		activFilter.removeAllElements();
		for(FilterItem fi:ph.getFilters()) {
			activFilter.addElement(fi.printReal());
		}
	}
}

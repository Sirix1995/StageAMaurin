/*
 * Copyright (C) 2013 GroIMP Developer Team
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

package de.grogra.imp.feedback;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.grogra.imp.feedback.FeedbackType.Types;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.util.I18NBundle;

/**
 * Opens a panel for providing feedback to the GroIMP developer team.
 * 
 */
public class FeedbackDialog extends JDialog {

	private static final long serialVersionUID = 34767834251L;
	private static FeedbackDialog dialog;
	private static I18NBundle thisI18NBundle;
	private JTextField nameField;
	private JTextField emailField;
	private JComboBox typeComboBox;
	private JTextArea commentArea;
	private JButton okButton;
	private JButton cancelButton;
	private JLabel statusField;
	private final static Font defaultFont = new Font("Dialog", 0, 12);
	private final static Font font1 = new Font("Dialog", 0, 23);


	private void openPanel(Context ctx) {
		nameField = new JTextField(23);
		emailField = new JTextField(23);
		FeedbackType fbt = new FeedbackType(thisI18NBundle);
		typeComboBox = new JComboBox(fbt.values());
		commentArea = new JTextArea();
		okButton = new JButton(thisI18NBundle.getString("feedback.send.Name"));
		cancelButton = new JButton(thisI18NBundle.getString("feedback.cancel.Name")); 
		statusField = new JLabel(thisI18NBundle.getString("feedback.thx.Name"));
		setModal(true);
		setResizable(false);
		setSize(500, 350);
		setTitle(thisI18NBundle.getString("feedback.title.Name"));
		// setIconImage(ImageHook.getImage("feedback.png"));

		makeLayout();
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				FeedbackAction fa = new FeedbackAction(dialog, thisI18NBundle);
				fa.sendFeedback(nameField.getText(), emailField.getText(), commentArea.getText(), getFeedbackType());
			}
		});
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		setVisible(true);
	}

	public Types getFeedbackType() {
		if(typeComboBox.getSelectedIndex() == -1) return FeedbackType.Types.General;
		return FeedbackType.getType(typeComboBox.getSelectedIndex());
	}

	private void makeLayout() {
		JPanel headerPanel = new JPanel();
		headerPanel.setBorder(BorderFactory.createEtchedBorder());
		headerPanel.setBackground(Color.WHITE);
		JLabel label1 = new JLabel(thisI18NBundle.getString("feedback.title.Name"));
		label1.setFont(font1);
		label1.setForeground(Color.BLACK);
		headerPanel.add(label1);

		JLabel nameLabel = new JLabel(thisI18NBundle.getString("feedback.dilog.name.Name")+" : ");
		nameLabel.setFont(defaultFont);
		JLabel eMailLabel = new JLabel(thisI18NBundle.getString("feedback.dialog.email.Name")+" : ");
		eMailLabel.setFont(defaultFont);
		JLabel typeLabel = new JLabel(thisI18NBundle.getString("feedback.dialog.type.Name")+" : ");
		typeLabel.setFont(defaultFont);
		typeComboBox.setFont(defaultFont);

		JPanel p0 = new JPanel();
		GroupLayout layout = new GroupLayout(p0);
		p0.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(nameLabel)
				.addComponent(eMailLabel)
				.addComponent(typeLabel))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(nameField)
				.addComponent(emailField)
				.addComponent(typeComboBox))
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(nameLabel)
				.addComponent(nameField))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(eMailLabel)
				.addComponent(emailField))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(typeLabel)
				.addComponent(typeComboBox))
		);

		JLabel commentLabel = new JLabel(thisI18NBundle.getString("feedback.dialog.comment.Name")+" : ");
		commentLabel.setFont(defaultFont);
		commentArea.setPreferredSize(new Dimension(0,140));

		JPanel okCancelPanel = new JPanel();
		okCancelPanel.setLayout(new GridLayout(1, 2, 8, 0));
		okButton.setFont(defaultFont);
		cancelButton.setFont(defaultFont);
		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);
		
		statusField.setFont(defaultFont);

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(Box.createHorizontalStrut(10));
		p2.add(statusField);
		p2.add(Box.createHorizontalGlue());
		p2.add(okCancelPanel);
		p2.add(Box.createHorizontalStrut(6));
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p3.add(Box.createHorizontalStrut(10));
		p3.add(commentLabel);
		p3.add(Box.createHorizontalGlue());

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		p1.add(headerPanel);
		p1.add(new JSeparator());
		p1.add(p0);
		p1.add(p3);
		p1.add(Box.createVerticalStrut(2));
		p1.add(new JScrollPane(commentArea));
		p1.add(new JSeparator());
		p1.add(Box.createVerticalStrut(4));
		p1.add(p2);
		p1.add(Box.createVerticalStrut(2));

		add(p1);
	}

	public void setStatus(String status) {
		statusField.setText(status);
		statusField.repaint();
	}

	public static void createFeedbackPanel (Item item, Object info, Context ctx) {
		thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();
		dialog = new FeedbackDialog();
		dialog.openPanel(ctx);
	}
}
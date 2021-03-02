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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import de.grogra.util.I18NBundle;

public class FeedbackAction {

	private final FeedbackDialog dialog;
	private static PreparedStatement insertFeedbackStmnt = null;
	private final I18NBundle thisI18NBundle;

	public FeedbackAction(FeedbackDialog dialog, I18NBundle thisI18NBundle) {
		this.dialog = dialog;
		this.thisI18NBundle = thisI18NBundle;
	}

	public void sendFeedback(final String name, final String eMail, final String comment, final FeedbackType.Types type) {
		Runnable committer = new Runnable() {
			@Override
			public void run() {
				if (comment.trim().equals("")) {
					JOptionPane.showMessageDialog(dialog, 
							thisI18NBundle.getString("feedback.action.message1.Name"), 
							thisI18NBundle.getString("feedback.action.message.type1.Name"), 1);
					return;
				}
				dialog.setStatus(thisI18NBundle.getString("feedback.action.status.message1.Name"));
				try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(dialog,
							thisI18NBundle.getString("feedback.action.message2.Name")+"\n"+e.toString(),
							thisI18NBundle.getString("feedback.action.message.type2.Name"), 0);
					return;
				}
				String url = thisI18NBundle.getString("feedback.action.server.Name");
				Connection con = null;
				try {
					con = DriverManager.getConnection(url,
							thisI18NBundle.getString("feedback.action.server.user.Name"),
							thisI18NBundle.getString("feedback.action.server.pw.Name"));
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(dialog,
							thisI18NBundle.getString("feedback.action.message3.Name")+"\n"+e.toString(), 
							thisI18NBundle.getString("feedback.action.message.type2.Name"), 0);
					return;
				}
				dialog.setStatus(thisI18NBundle.getString("feedback.action.status.message2.Name"));
				try {
					insertFeedbackEntry(con, name, eMail, comment, type);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(dialog, 
							thisI18NBundle.getString("feedback.action.message4.Name")+"\n"+e.toString(), 
							thisI18NBundle.getString("feedback.action.message.type2.Name"), 0);
					return;
				}
				dialog.setStatus(thisI18NBundle.getString("feedback.action.status.message3.Name"));
				JOptionPane.showMessageDialog(dialog, 
						thisI18NBundle.getString("feedback.action.message5.Name"), 
						thisI18NBundle.getString("feedback.action.message.type3.Name"), 1);
				dialog.setVisible(false);
			}
		};
		Thread t = new Thread(committer, "Feedback commit thread");
		t.start();
	}

	protected void insertFeedbackEntry(Connection c, String name, String eMail, String comment, FeedbackType.Types type)
			throws SQLException {
		if (insertFeedbackStmnt == null || insertFeedbackStmnt.getConnection() != c) {
			String com = "INSERT INTO main (`name`, `email`, `comment`, `type`, `version`, `os`) values (?, ?, ?, ?, ?, ?)";
			insertFeedbackStmnt = c.prepareStatement(com);
		}
		insertFeedbackStmnt.setString(1, name);
		insertFeedbackStmnt.setString(2, eMail);
		insertFeedbackStmnt.setString(3, comment);
		insertFeedbackStmnt.setInt(4, type.ordinal());
		//additional informations
		insertFeedbackStmnt.setString(5, thisI18NBundle.getString("app.version.Name"));
		insertFeedbackStmnt.setString(6, System.getProperty("os.name"));
		insertFeedbackStmnt.executeUpdate();
	}

}

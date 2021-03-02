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

import de.grogra.util.I18NBundle;

public final class FeedbackType {

	private final I18NBundle thisI18NBundle;

	public static enum Types {
		General, Suggestion, Question, Request, Bug
	}

	public FeedbackType(I18NBundle thisI18NBundle) {
		this.thisI18NBundle = thisI18NBundle;
	}

	private String value(Types x) {
		switch(x) {
			case General:
				return thisI18NBundle.getString("feedback.feedbacktype.general.Name");
			case Suggestion:
				return thisI18NBundle.getString("feedback.feedbacktype.suggestion.Name");
			case Question:
				return thisI18NBundle.getString("feedback.feedbacktype.question.Name");
			case Request:
				return thisI18NBundle.getString("feedback.feedbacktype.request.Name");
			case Bug:
				return thisI18NBundle.getString("feedback.feedbacktype.bug.Name");
			}
		return thisI18NBundle.getString("feedback.feedbacktype.general.Name");
	}

	public String[] values() {
		Types[] t = Types.values();
		String[] s = new String[t.length];
		for (int i = 0; i < t.length; i++) {
			s[i] = value(t[i]);
		}
		return s;
	}

	public static Types getType(int i) {
		if(i<0 || i>Types.values().length) return Types.General;
		return Types.values()[i];
	}
}

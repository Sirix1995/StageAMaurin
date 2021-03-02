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

import java.awt.Font;

public class ComponentDescriptionFont {

	public static final String fontType = "Times New Roman";
	//public static final String fontTypeCode = "Courier New";
	public static final String fontTypeCode = "DialogInput";
	
	private Font fontHeader1;
	private Font fontHeader2;
	private Font fontHeader3;
	private Font fontHeader4;
	private Font fontNormal;
	
	private Font fontCodeNormal;
	private Font fontCodeMini;
	
	public ComponentDescriptionFont()
	{
		setFontHeader1(new Font(fontType, Font.BOLD, 17));
		setFontHeader2(new Font(fontType, Font.BOLD, 15));
		setFontHeader3(new Font(fontType, Font.BOLD, 14));
		setFontHeader4(new Font(fontType, Font.BOLD, 12));
		setFontNormal(new Font(fontType, Font.PLAIN, 12));
		
		this.setFontCodeNormal(new Font(fontTypeCode, Font.PLAIN, 12));
		this.setFontCodeMini(new Font(fontTypeCode, Font.PLAIN, 11));
	}

	public Font getFontHeader1() {
		return fontHeader1;
	}

	public void setFontHeader1(Font fontHeader1) {
		this.fontHeader1 = fontHeader1;
	}

	public Font getFontHeader2() {
		return fontHeader2;
	}

	public void setFontHeader2(Font fontHeader2) {
		this.fontHeader2 = fontHeader2;
	}

	public Font getFontHeader3() {
		return fontHeader3;
	}

	public void setFontHeader3(Font fontHeader3) {
		this.fontHeader3 = fontHeader3;
	}

	public Font getFontHeader4() {
		return fontHeader4;
	}

	public void setFontHeader4(Font fontHeader4) {
		this.fontHeader4 = fontHeader4;
	}

	public Font getFontNormal() {
		return fontNormal;
	}

	public void setFontNormal(Font fontNormal) {
		this.fontNormal = fontNormal;
	}

	public Font getFontCodeNormal() {
		return fontCodeNormal;
	}

	public void setFontCodeNormal(Font fontCodeNormal) {
		this.fontCodeNormal = fontCodeNormal;
	}

	public Font getFontCodeMini() {
		return fontCodeMini;
	}

	public void setFontCodeMini(Font fontCodeMini) {
		this.fontCodeMini = fontCodeMini;
	}
	
	
}

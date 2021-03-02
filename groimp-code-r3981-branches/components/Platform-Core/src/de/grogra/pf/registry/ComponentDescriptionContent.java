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

import java.util.ArrayList;

public class ComponentDescriptionContent {

	private String m_name;
	private final ArrayList<String> m_description;
	private String m_date;
	private String m_author;
	private String m_version;
	private String m_return;
	private final ArrayList<String> m_param;
	private final ArrayList<String> m_specifiers;
	private final ArrayList<String> m_inSlot;
	private final ArrayList<String> m_outSlot;
	private boolean m_isMethod;
	
	public static final String XML_TAG_MODULE 					= "module";
	public static final String XML_TAG_MODULE_SPECIFIERS	 	= "moduleSpecifiers";
	public static final String XML_TAG_MODULE_SPECIFIER_ELEMENT	= "moduleSpecifierElement";
	public static final String XML_TAG_MODULE_PARAMETERS	 	= "moduleParameters";
	public static final String XML_TAG_MODULE_PARAMETER_ELEMENT	= "moduleParameterElement";
	public static final String XML_TAG_MODULE_INSLOTS		 	= "moduleInSlots";
	public static final String XML_TAG_MODULE_INSLOTS_ELEMENT	= "moduleInSlotsElement";
	public static final String XML_TAG_MODULE_OUTSLOTS		 	= "moduleOutSlots";
	public static final String XML_TAG_MODULE_OUTSLOTS_ELEMENT	= "moduleOutSlotsElement";
	
	public static final String XML_TAG_CONTENT 					= "content";
	
	public static final String XML_TAG_NAME 			= "name";
	public static final String XML_TAG_DATE 			= "date";
	public static final String XML_TAG_AUTHOR 			= "author";
	public static final String XML_TAG_DESCRIPTION 		= "description";
	public static final String XML_TAG_VERSION			= "version";
	public static final String XML_TAG_PARAMETER		= "parameter";
	public static final String XML_TAG_SPECIFIER		= "specifier";
	public static final String XML_TAG_RETURN			= "return";
	public static final String XML_TAG_ISMETHOD			= "isMethod";
	
	public static final String PANEL_NAME				= "component description panel";
	
	public ComponentDescriptionContent()
	{
		setName(new String(""));
		setDate(new String(""));
		setAuthor(new String(""));
		setVersion(new String(""));
		setReturn(new String(""));
		m_description = new ArrayList<String>();
		m_param = new ArrayList<String>();
		m_inSlot = new ArrayList<String>();
		m_outSlot = new ArrayList<String>();
		m_specifiers = new ArrayList<String>();
		m_isMethod=false;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String m_name) {
		this.m_name = m_name;
	}

	public String getDate() {
		return m_date;
	}

	public void setDate(String m_date) {
		this.m_date = m_date;
	}

	public String getAuthor() {
		return m_author;
	}

	public void setAuthor(String m_author) {
		this.m_author = m_author;
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String m_version) {
		this.m_version = m_version;
	}

	public String getReturn() {
		return m_return;
	}

	public void setReturn(String m_return) {
		this.m_return = m_return;
	}
	
	public int getDescriptionCount()
	{
		return m_description.size();
	}
	
	public int getParamCount()
	{
		return m_param.size();
	}
	
	public int getInSlotCount()
	{
		return m_inSlot.size();
	}
	
	public int getOutSlotCount()
	{
		return m_outSlot.size();
	}
	
	public int getSpecifierCount()
	{
		return m_specifiers.size();
	}
	
	public void addDescription(String description)
	{
		m_description.add(description);
	}
	
	public void addParam(String param)
	{
		m_param.add(param);
	}
	
	public void addInSlot(String inSlot)
	{
		m_inSlot.add(inSlot);
	}
	
	public void addOutSlot(String outSlot)
	{
		m_outSlot.add(outSlot);
	}
	
	public void addSpecifier(String specifier)
	{
		m_specifiers.add(specifier);
	}
	
	public String getDescription(int index)
	{
		return m_description.get(index);
	}
	
	public String getParam(int index)
	{
		return m_param.get(index);
	}
	
	public String getInSlot(int index)
	{
		return m_inSlot.get(index);
	}
	
	public String getOutSlot(int index)
	{
		return m_outSlot.get(index);
	}
	
	public String getSpecifier(int index)
	{
		return m_specifiers.get(index);
	}
	
	public String getDescriptionInSingleString()
	{
		String res = new String();
		for(int i=0; i<m_description.size(); ++i)
		{
			if(i==0)
				res+=m_description.get(i);
			else
				res = res + " " + m_description.get(i);
		}
		return res;
	}

	public boolean isMethod() {
		return m_isMethod;
	}

	public void setIsMethod(boolean m_isMethod) {
		this.m_isMethod = m_isMethod;
	}

}

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

package de.grogra.pf.registry;

import java.util.ArrayList;

public class MethodDescriptionContent implements Comparable<MethodDescriptionContent> {

	private String m_name;
	private String m_return;
	private String m_visibility;
	private String m_type;
	private String m_returncomment;
	private boolean m_static;

	private final ArrayList<String> m_annotation;
	private final ArrayList<String> m_attributeParameter;
	private final ArrayList<String> m_description;
	private final ArrayList<String> m_parameter;
	private final ArrayList<String> m_see;

	public static final String XML_TAG_METHOD					= "method";
	
	public static final String XML_TAG_METHOD_ATTRIBUTE			= "attribute";
	public static final String XML_TAG_METHOD_ANNOTATION		= "annotation";
	public static final String XML_TAG_METHOD_DESCRIPTION		= "description";
	public static final String XML_TAG_METHOD_PARAMETER			= "parameter";


	public MethodDescriptionContent() {
		m_name = "";
		m_return = "";
		m_visibility = "";
		m_type = "";
		m_returncomment = "";
		m_static = false;

		m_description = new ArrayList<String>();
		m_parameter = new ArrayList<String>();
		m_see = new ArrayList<String>();
		m_annotation = new ArrayList<String>();
		m_attributeParameter = new ArrayList<String>();
	}

	public void addAnnotation(String param) {
		m_annotation.add(param);
	}
	
	public void addAttributeParameter (String param) {
		m_attributeParameter.add(param);
	}


	public void addDescription(String description)
	{
		m_description.add(description);
	}

	public void addParameter(String param)
	{
		m_parameter.add(param);
	}
	
	public void addSee(String param)
	{
		m_see.add(param);
	}

	@Override
	public int compareTo(MethodDescriptionContent b) {
		if (b.getName() == null && this.getName() == null) {
			return 0;
		}
		if (this.getName() == null) {
			return 1;
		}
		if (b.getName() == null) {
			return -1;
		}
		return this.getName().compareToIgnoreCase (b.getName());
	}
	
	public ArrayList<String> getAnnotation() {
		return m_annotation;
	}
	
	public ArrayList<String> getAttributeParameter () {
		return m_attributeParameter;
	}


	public ArrayList<String> getDescription() {
		return m_description;
	}

	public String getName() {
		return m_name;
	}

	public ArrayList<String> getParameter() {
		return m_parameter;
	}
	
	public String getReturn() {
		return m_return;
	}

	/**
	 * @return the m_returncomment
	 */
	public String getReturncomment () {
		return m_returncomment;
	}

	public ArrayList<String> getSee() {
		return m_see;
	}
	/**
	 * @return the m_type
	 */
	public String getType () {
		return m_type;
	}

	/**
	 * @return the m_visibility
	 */
	public String getVisibility () {
		return m_visibility;
	}

	/**
	 * @return the m_static
	 */
	public boolean isStatic () {
		return m_static;
	}

	public void setName(String m_name) {
		this.m_name = m_name;
	}

	public void setReturn(String m_return) {
		this.m_return = m_return;
	}

	public void setReturncomment (String value) {
		m_returncomment = value;
	}

	public void setStatic (boolean b) {
		m_static = b;
	}

	public void setType (String value) {
		m_type = value;
	}

	public void setVisibility (String value) {
		m_visibility = value;
	}

}

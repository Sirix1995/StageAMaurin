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

package de.grogra.imp2d.graphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.ButtonProperty;
import de.grogra.pf.ui.edit.ExtraProperties;
import de.grogra.pf.ui.edit.Property;
import de.grogra.reflect.Type;
import de.grogra.util.I18NBundle;
import de.grogra.vfs.MemoryFileSystem;

public class GroIMPComponent extends Node implements ExtraProperties, Command {

	/**
	 * Default serial ID.
	 */
	private static final long serialVersionUID = 123581321L;

	private static I18NBundle thisI18NBundle;

	/**
	 * Indicates the state of a component at runtime. It can be: <code>OK_STATE</code>, <code>WARNING_STATE</code>, or <code>ERROR_STATE</code>
	 */
	public int runtimeStatus = OK_STATE; 

	/**
	 * Indicates a normal state of a component at runtime with no warnings and errors.
	 */
	public static final int OK_STATE = 0;
	/**
	 * Indicates a state of a component at runtime with warnings.
	 */
	public static final int WARNING_STATE = 1;
	/**
	 * Indicates a normal state of a component at runtime with no warnings and errors.
	 */
	public static final int ERROR_STATE = 2;

	/**
	 * Indicates the state at runtime of this component. It can be: <code>OK_STATE</code>, <code>WARNING_STATE</code>, or <code>ERROR_STATE</code>
	 * 
	 * @return status code
	 */
	public int getRuntimeStatus ()
	{
		return runtimeStatus;
	}
	
	private final Registry registry = Registry.current ();
	
	
	public GroIMPComponent() {
		if(Registry.current () != null)
			thisI18NBundle = Registry.current ().getPluginDescriptor("de.grogra.imp2d").getI18NBundle();
		
		initComponent();
	}
	
	/**
	 * Returns the refinement parent of this node.
	 * The underlying tree structure is defined by the edges
	 * {@link Graph#REFINEMENT_EDGE}.
	 *
	 * @return the refinement parent
	 */
	public Node getParent() {
		return findAdjacent (true, false, Graph.REFINEMENT_EDGE); 
	}
	
	@Override
	public List<Node> getInputSlotNodes() {
		return getSlotNodes(12);
	}

	@Override
	public List<Node> getOutputSlotNodes() {
		return getSlotNodes(11);
	}

	private List<Node> getSlotNodes(int edgeType) {
		List<Node> list = new ArrayList<Node>();
		for (Edge e = getFirstEdge(); e != null; e = e.getNext(this)) {
			if (e.isSource(this) && e.getEdgeBits ()==edgeType) {
				list.add (e.getTarget ());
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return getClass().getName ()+"[id="+getId()+"]:name= "+getName ();
	}
	
	/**
	 * If there occurred a warning message will be stored in this field.
	 */
	Vector<String> warningMessageVector = new Vector<String>();
	
	/**
	 * If there occurred a warning message will be stored in this field.
	 */	
	Vector<String> errorMessageVector = new Vector<String>();

	
	/**
	 * List of all input slots.
	 */
	Vector<String> listOfInputSlots = new Vector<String>();
	/**
	 * List of all output slots.
	 */
	Vector<String> listOfOutputSlots = new Vector<String>();


	@Override
	public void fieldModified (PersistenceField field, int[] indices, Transaction t) {
		super.fieldModified (field, indices, t);
		if (!Transaction.isApplying(t)) {
			updateCodeFile(field);
			selfTest();
		}
	}

	private void updateCodeFile(PersistenceField field) {
		MemoryFileSystem fs = (MemoryFileSystem) registry.getFileSystem();
		String fileName = "";
		if(field==null) {
			fileName = ""+this.getClass ();
			if(fileName.length ()<6 || fileName.indexOf ("$")==-1) return;
			fileName = fileName.substring (6, fileName.indexOf ("$"));
			rewriteAllFields(fs, fileName, getFileCode(fs, fileName));
		} else {
			ManageableType.Field ff = field.getSubfield(0);
			fileName = ""+ff.getClass ();
			if(fileName.length ()<6 || fileName.indexOf ("$")==-1) return;
			fileName = fileName.substring (6, fileName.indexOf ("$"));
			rewriteField(fs, fileName, getFileCode(fs, fileName), field);
		}
	}


	private void rewriteAllFields(MemoryFileSystem fs, String fileName, String fileCode) {
		if(fileCode==null) return;

		//search pattern for the field
		String searchPattern = "(protected void resetParameters)([ \t]*\\(\\)[ \t]*\\{)(\\s|\\S)*?(\\})";
		
		// search for occurrences 
		Pattern pattern = Pattern.compile(searchPattern);
		Matcher matcher = pattern.matcher(fileCode);
		List<String> listMatches = new ArrayList<String>();
		while(matcher.find()) {
			listMatches.add(matcher.group(0));
		}
		// check how often it occurred 
		if(listMatches.size ()!=1) {
			System.out.println ("Error in GroIMPComponent.rewriteAllFields()");
			System.out.println ("Found "+listMatches.size ()+" occurrences of function resetParameters() in code fiel "+fileName+".rgg");
		}
		
		String resetParametersCode = listMatches.get (0);
		//pattern for variables
		searchPattern = "(.*)(=).*(;)" ;
		
		//extract list of variables
		Pattern pattern2 = Pattern.compile(searchPattern);
		Matcher matcher2 = pattern2.matcher(resetParametersCode);
		
		String variDecl = "", variName = "", mainVariDecl = "", newDecl = "";
		//for all variables
		while(matcher2.find()) {
			// extract the name of it
			variDecl = matcher2.group(0);
			variDecl = variDecl.replaceAll (" |\t", "");//remove whitespaces
			variName = variDecl.substring (0, variDecl.indexOf ("="));
			//search it in the code
			searchPattern = "(double|float|int|byte|long|String|boolean)(.*)("+variName+")(.*)(=).*(;)";
			pattern = Pattern.compile(searchPattern);
			matcher = pattern.matcher(fileCode);
			if (matcher.find()) {
				mainVariDecl = matcher.group (0);
			
				newDecl = mainVariDecl.substring (0,mainVariDecl.indexOf("=")).trim ()
						+" = "+variDecl.substring (variDecl.indexOf ("=")+1,variDecl.length ());
				
				//replace the code for the field
				fileCode = fileCode.replaceFirst (searchPattern, newDecl);
			}
		}

		//writing it back
		writeFile(fs, fileCode, fileName);
		//refresh JEdit text editor
		Workbench.refreshJEdit(fileName+".rgg");
	}
	
	private void rewriteField(MemoryFileSystem fs, String fileName, String fileCode, PersistenceField field) {
		if(fileCode==null) return;

		Type<?> type = field.getType ();
		String fieldName = field.getLastField().getName ();
		String typeName = type.getSimpleName ();
		String value = "";
		try {
			if(typeName.equals ("double")) {
				value = ""+field.getDouble (this);
			} else if(typeName.equals ("int")) {
				value = ""+field.getInt (this);
			} else if(typeName.equals ("float")) {
				value = ""+field.getFloat (this);
			} else if(typeName.equals ("byte")) {
				value = ""+field.getByte (this);
			} else if(typeName.equals ("String")) {
				value = "\""+field.getObject (this)+"\"";
			} else if(typeName.equals ("long")) {
				value = ""+field.getLong (this);
			} else if(typeName.equals ("boolean")) {
				value = ""+field.getBoolean (this);
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		//search pattern for the field
		String searchPattern = "("+typeName+")(.*)("+fieldName+")(.*)(=)(.*)(;)";
		
		// search for occurrences 
		Pattern pattern = Pattern.compile(searchPattern);
		Matcher matcher = pattern.matcher(fileCode);
		List<String> listMatches = new ArrayList<String>();
		while(matcher.find()) {
			listMatches.add(matcher.group(0));
		}
		// check how often it occurred 
		if(listMatches.size ()!=1) {
			System.out.println ("Error in GroIMPComponent.rewriteField()");
			System.out.println ("Found "+listMatches.size ()+" occurrences of parameter "+fieldName+" in code fiel "+fileName+".rgg");
			System.out.println ("Will repllace only the first  occurrences.");
		}

		//replace the code for the field
		fileCode = fileCode.replaceFirst (searchPattern, typeName+" "+fieldName+" = "+value+";");
//		fileCode = fileCode.replace (listMatches.get (0), typeName+" "+fieldName+" = "+value+";");

		//writing it back
		writeFile(fs, fileCode, fileName);
		//refresh JEdit text editor
		Workbench.refreshJEdit(fileName+".rgg");
	}

	private void writeFile (MemoryFileSystem fs, String fileCode, String fileName) {
		OutputStream out = fs.getOutputStream(fs.getFile(fileName+".rgg"), false);
		try {
			out.write(fileCode.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileCode(MemoryFileSystem fs, String fileName) {
		// read input file
		Object f = fs.getFile(fileName+".rgg");
		if(f==null) {
			System.out.println ("Error in GroIMPComponent fiel "+fileName+" not found.");
			return null;
		}
		InputStream is = fs.getInputStream(f);
		if(is==null) {
			System.out.println ("Error in GroIMPComponent fiel input stream for file "+fileName+" not found.");
			return null;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb=new StringBuilder();
		String read = "";
		try {
			read = br.readLine();
			while(read != null) {
				sb.append(read+"\n");
				read = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Init-function
	 * 
	 */
	protected void initComponent () {
		warningMessageVector.clear ();
		errorMessageVector.clear ();
		
		listOfInputSlots.clear ();
		listOfOutputSlots.clear ();
		
		setSlots();
	}

	
	/**
	 * 
	 * 
	 */
	public void removeComponent () {
		registry.removeComponent(getClass().getSimpleName());
	}

	/**
	 * Declaration of all input and output slots.
	 * Usually encapsulates a list of <code>addInputSlot()</code> and <code>addOutputSlot()</code> calls.
	 */
	public void setSlots() {}

	/**
	 * Declaration of a output slot.
	 */
	public void addOutputSlot(String name) {
		registry.addOutputSlot(getClass().getSimpleName(), name);
		listOfOutputSlots.add(name);
	}
	
	/**
	 * Declaration of a input slot.
	 */
	public void addInputSlot(String name) {
		registry.addInputSlot(getClass().getSimpleName(), name);
		listOfInputSlots.add(name);
	}

	public int getNumberOfInputSlots () {
		return listOfInputSlots.size();
	}

	public int getNumberOfOutputSlots () {
		return listOfOutputSlots.size();
	}

	public Vector<String> getListOfInputSlots () {
		return listOfInputSlots;
	}

	public Vector<String> getListOfOutputSlots () {
		return listOfOutputSlots;
	}
	
	/**
	 * Resets all model parameters to there default values.  
	 */
	protected void resetParameters() {
		initComponent ();
	}

	/**
	 * Gets the message of warnings occurred, otherwise the result vector will be empty.
	 * 
	 * @return message vector of current problems
	 */
	public Vector<String> getWarningMessageVector ()
	{
		return warningMessageVector;
	}
	
	/**
	 * Gets the message of errors occurred, otherwise the result vector will be empty.
	 * 
	 * @return message vector of current problems
	 */
	public Vector<String> getErrorMessageVector ()
	{
		return errorMessageVector;
	}	
		
	/**
	 * Checks, if all required input slots are fulfilled. 
	 * 
	 * @return status of required services
	 */
	protected final boolean requiredServicesFulfilled() {
		//for all input slots
		for (String item : listOfOutputSlots) {
			//check if ok
			if(!item.equals (item)) {
				//if not: addWarning("required services is missing! "+servisI.getName());
				addWarning("required services is missing! "+item);
			}
		}
		return true;
	}

	/**
	 * Performers a self test of this component by calling <code>checkPreconditions()</code> and <code>checkPostconditions()</code> and 
	 * returning the logical and of both functions. 
	 * 
	 * @return status of self test
	 */
	public final boolean selfTest() {
		boolean b = requiredServicesFulfilled() && checkPreconditions() && checkPostconditions();
		if (!b) printMessages(); 
		return b;
	}

	/**
	 * Check it all input values (model parameters and variables) are in an acceptable ranges and of the right type.
	 * It should check different combinations of input values - especially extremes of ranges needs to be checked.
	 * 
	 * By default false until the component developer has implemented this required function.
	 *
	 * @return status of pre condition test 
	 */
	protected boolean checkPreconditions() {
		return false;
	}


	/**
	 * Check it all output values are in an acceptable ranges.
	 * It should check different combinations of input values - especially extremes of ranges needs to be checked.
	 * 
	 * By default false until the component developer has implemented this required function.
	 *
	 * @return status of pre condition test 
	 */
	protected boolean checkPostconditions() {
		return false;
	}
	
	/**
	 * Check if the value is in an acceptable range.
	 * TRUE iff it is in rage, otherwise FALSE.
	 *
	 * @return status of this check
	 */	
	protected boolean checkRange(double value, double min, double max) {
		if (value < min) {
			addWarning("Value out of range. ("+value+" is smaller then the specified minimum of "+min+".");
			return false;
		}
		if (value > max) {
			addWarning("Value out of range. ("+value+" is largler then the specified maximum of "+max+".");
			return false;
		} 
		return true;
	}

	/**
	 * Returns a list of key words which identifies and/or characterises this component.
	 * 
	 * @return list of key words
	 */
	protected String getKeyWords() {
		return "Base component";
	}

	/**
	 * Returns a short Description which identifies and/or characterises this component.
	 * 
	 * @return list of key words
	 */
	protected String getShortDescription() {
		return "base class for all components";
	}

	
	/**
	 * Returns a snippet of code for the Axiom line to insert this component to the main graph.
	 * 
	 * @return snippet of code
	 */
	protected String getAxiomCode() {
		return "";
	}

	/**
	 * Returns executable XL code to insert this component as a node to the main graph.
	 * 
	 * @return snippet of code
	 */
	protected String addNodeToGraoh() {
		return "";
	}
	
	protected void addWarning(String warningMessage) {
		runtimeStatus = WARNING_STATE;
		warningMessageVector.addElement (warningMessage +"\n");
	}

	protected void addError(String errorMessage) {
		runtimeStatus = ERROR_STATE;
		errorMessageVector.addElement (errorMessage +"\n");
	}
	

	public void printMessages() {
		printWarningMessages();
		printErrorMessages();
	}
	
	private void printWarningMessages() {
		Vector<String> msgVec = getWarningMessageVector();
		if(msgVec.size ()>0) {
			Registry.current ().getLogger ().log (Workbench.SOFT_GUI_INFO,
				thisI18NBundle.msg ("xl.component-warning", getClass ().getName (), "\n"+msgVec));
		}
	}
	
	private void printErrorMessages() {
		Vector<String> msgVec = getErrorMessageVector();
		if(msgVec.size ()>0) {
			Registry.current ().getLogger ().log (Workbench.SOFT_GUI_INFO,
				thisI18NBundle.msg ("xl.component-error", getClass ().getName (), "\n"+msgVec));
		}
	}

	@Override
	public List<Property> GetExtraProperties(Context c)
	{
		ArrayList<Property> list = new ArrayList<Property>();
		list.add(new ButtonProperty(c, "Reset", this)); 
		return list;
	}

	@Override
	public String getCommandName ()
	{
		return toString();
	}

	@Override
	public void run (Object info, Context context)
	{
		resetParameters();
		updateCodeFile(null);
		selfTest();
	}

	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new GroIMPComponent ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new GroIMPComponent ();
	}

//enh:end

}

package de.grogra.mtg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.ReaderSourceImpl;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.reflect.Type;
import de.grogra.rgg.model.CompilationFilter;
import de.grogra.rgg.model.XLFilter;
import de.grogra.util.MimeType;

public class MTGModuleBuilder 
{
	private MTGNode rootNode;
	
	private String systemId;
	
	private String errorMessage;
	
	private String xlFileNameWithoutExt;
	
	public MTGModuleBuilder()
	{	
	}
	
	public MTGModuleBuilder(MTGNode root,String sysId, String xlFile)
	{
		this.rootNode = root;
		this.systemId = sysId;
		this.errorMessage = "";
		this.xlFileNameWithoutExt = xlFile;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	
	public int writeTypes(BufferedWriter writer)
	{
		//check if MTG root node exists
		if(rootNode==null)
		{
			errorMessage = "Root node is null.";
			return MTGError.MTG_MODULE_WRITE_ERROR_NO_ROOT_NODE;
		}
		//check if writer is valid
		if(writer==null)
		{
			errorMessage = "BufferedWriter is null.";
			return MTGError.MTG_MODULE_WRITE_ERROR_INVALID_BUFFEREDWRITER;
		}
		
		try
		{
			//Obtain classes and feature attributes from root node
			Object mtgClassesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
			if(mtgClassesObj==null)
			{
				errorMessage = "Missing MTG classes information.";
				return MTGError.MTG_MODULE_WRITE_ERROR_NO_CLASSES;
			}
			ArrayList<MTGNodeDataClasses> mtgClasses = (ArrayList<MTGNodeDataClasses>)mtgClassesObj;
			
			ArrayList<MTGNodeDataFeature> mtgFeatures;
			Object mtgFeaturesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
			if(mtgFeaturesObj==null)
				mtgFeatures = new ArrayList<MTGNodeDataFeature>();
			else
				mtgFeatures = (ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
			
			writer.write("/* 																															\n");
			writer.write("* //This is a generated XL file.																								\n");
			writer.write("* //NOTE: Do not edit the contents of this file																				\n");			
			writer.write("* //NOTE: Invoke method 'has<standard attribute identifier>' to check if value is specified in MTG file.						\n");
			writer.write("* 																															\n");		
			writer.write("* //Standard MTG features in all generated nodes																				\n");			
			writer.write("* 																															\n");		
			writer.write("* //Triangular Coordinate System values																						\n");		
			writer.write("* public double L1;					//TR_X																					\n");												
			writer.write("* public double L2;					//TR_Y																					\n");									
			writer.write("* public double L3;					//TR_Z																					\n");												
			writer.write("* public double DAB;				//TR_DAB										 // Distance between points A and B.		\n");							
			writer.write("* public double DAC;				//TR_DAC										 // Distance between points A and C.		\n");		
			writer.write("* public double DBC;				//TR_DBC										 // Distance between points B and C.		\n");		
			writer.write("* 																															\n");				
			writer.write("* // Attributes containing the coordinates in a Cartesian system of reference.												\n");					
			writer.write("* public double XX;					//CA_X																					\n");						
			writer.write("* public double YY;					//CA_Y																					\n");								
			writer.write("* public double ZZ;					//CA_Z																					\n");								
			writer.write("* 																															\n");				
			writer.write("* //Attributes containing measures.																							\n");						
			writer.write("* public double Length;				//ATT_LENGTH      //NOTE: super class F already contains attribute length				\n");							
			writer.write("* public double Azimut;				//ATT_AZIMUT																			\n");							
			writer.write("* public double Alpha;				//ATT_ALPHA																				\n");							
			writer.write("* public double AA;					//ATT_TETA																				\n");												
			writer.write("* public double BB;					//ATT_PHI																				\n");				
			writer.write("* public double CC;					//ATT_PSI																				\n");		
			writer.write("* public double TopDia;				//ATT_TOPDIA																			\n");		
			writer.write("* public double BotDia;				//ATT_BOTTOMDIA																			\n");			
			writer.write("* public double Position;				//ATT_POSITION																			\n");
			writer.write("* public int Category;				//ATT_CATEGORY																			\n");				
			writer.write("* public Vector3d DirectionPrimary;	//ATT_DIRECTION_PRI																		\n");		
			writer.write("* public int Order;					//ATT_ORDER																				\n");
			writer.write("* 																															\n");							
			writer.write("*/                                                                                                                            \n");
			
			//string representing code to be passed to compiler
			writer.write("import de.grogra.mtg.MTGNode;\n\n");
			//writer.write("import de.grogra.turtle.F;\n\n");
			
			for(int i=0; i<mtgClasses.size(); ++i)
			{	
				//begin module code
				MTGNodeDataClasses cls = mtgClasses.get(i);
				String clsSym = cls.getSymbol();
		
				writer.write("module " + MTGKeys.getGeneratedModuleName(clsSym) + " extends MTGNode {\n");
				//writer.write("module " + MTGKeys.getGeneratedModuleName(clsSym) + " extends F {\n");
				
				//add in attributes
				for(int j=0; j<mtgFeatures.size(); ++j)
				{
					MTGNodeDataFeature feature = mtgFeatures.get(j);
					String featureName = feature.getFeatureName();
					if(!MTGKeys.isStandardAttribute(featureName))
					{
						String featureJavaType = MTGKeys.codeToJavaTypeFeatureTypes(feature.getFeatureTypeIndex());
						if(featureJavaType==null)
						{
							errorMessage = "Feature value type with index " + feature.getFeatureNameIndex() + " not recognized.";
							return MTGError.MTG_MODULE_WRITE_ERROR_UNRECOGNIZED_FEATURE_TYPE;
						}
						
						//declare attribute in module
						writer.write("    public " + featureJavaType + " " + featureName + ";\n\n");
						//declare boolean flag to indicate if value exists in MTG file
						writer.write("    public boolean has" + featureName + ";\n\n");
						
						//create set method
						writer.write("    public void set" + featureName + "(" + featureJavaType + " val)\n    {\n        this." + featureName + "=val;\n    }\n\n");
						//create get method
						writer.write("    public " + featureJavaType + " get" + featureName + "()" + "\n    {\n        return this." + featureName + ";\n    }\n\n");
					
						//create set method for flag
						writer.write("    public void setHas" + featureName + "(boolean val)\n    {\n        this.has" + featureName + "=val;\n    }\n\n");
						//create get method
						writer.write("    public boolean has" + featureName + "()" + "\n    {\n        return this.has" + featureName + ";\n    }\n\n");
					}
				}
				
				//close module code
				writer.write("}\n\n");
			}
			//return success code
			return MTGError.MTG_MODULE_WRITE_SUCCESSFUL;
		}
		catch(Throwable t)
		{
			errorMessage = t.getMessage();
			//return error code
			return MTGError.MTG_MODULE_WRITE_ERROR;
		}
	}
	
	public int compileTypes(BufferedReader reader)
	{
		//check if reader is valid
		if(reader==null)
		{
			errorMessage = "BufferedReader is null.";
			return MTGError.MTG_MODULE_COMPILE_ERROR_INVALID_BUFFEREDREADER;
		}
		
		//String for accumulated code read from the file
		String gCode = "";
		//String for each line read from the file
		String gLine=null;
		
		//read code from file
		try 
		{
			//loop to read and buffer code in gCode
			gLine = reader.readLine();
			while(gLine!=null)
			{
				gCode = gCode+gLine;
				gLine = reader.readLine();
			}
		} 
		catch (IOException e) {
			errorMessage = e.getMessage();
			return MTGError.MTG_MODULE_COMPILE_ERROR_READ_FILE;
		}
		
		//compile code
		try
		{
			//compile
			Type[] types = XLFilter.compile(new StringReader(gCode),this.systemId);
			//FilterItem fitem = new filterItem();
			//XLFilter xlFilter = new XLFilter (null, new ReaderSourceImpl (new StringReader(gCode.toString()), xlFileNameWithoutExt, new MimeType ("text/x-grogra-xl",null), Registry.current (), null));	
			//CompilationFilter compilationFilter = new CompilationFilter (null, xlFilter);
			//Type[] types = compilationFilter.compile(null, null);
			
			//put types in root node for retrieval by graph builder/translator later when loading body section of MTG file
			if(types!=null)
				((MTGRoot)rootNode).setObject(MTGKeys.MTG_RGG_MODULES, types);
			
			//put types in registry for recognition by project
			Registry r = Registry.current();
			for(int i=0; i<types.length; ++i)
				r.getDirectory ("/classes", null).add(new TypeItem(types[i]));
		}
		catch(Throwable t)
		{
			errorMessage = t.getMessage();
			return MTGError.MTG_MODULE_COMPILE_ERROR_COMPILATION;
		}
		
		//return success code
		return MTGError.MTG_MODULE_COMPILE_SUCCESSFUL;
	}
	
	/*
	public Type[] createTypes()
	{
		if(rootNode==null)
			return null;
		
		try
		{
			//Obtain classes and feature attributes from root node
			Object mtgClassesObj = rootNode.getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
			if(mtgClassesObj==null)
				return null;
			ArrayList<MTGNodeDataClasses> mtgClasses = (ArrayList<MTGNodeDataClasses>)mtgClassesObj;
			
			ArrayList<MTGNodeDataFeature> mtgFeatures;
			Object mtgFeaturesObj = rootNode.getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
			if(mtgFeaturesObj==null)
				mtgFeatures = new ArrayList<MTGNodeDataFeature>();
			else
				mtgFeatures = (ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
			
			//string representing code to be passed to compiler
			String code = "import de.grogra.mtg.MTGNode;";
			
			for(int i=0; i<mtgClasses.size(); ++i)
			{	
				//begin module code
				MTGNodeDataClasses cls = mtgClasses.get(i);
				String clsSym = cls.getSymbol();
				code = code + "module " + clsSym + " extends MTGNode {";
				
				//add in attributes
				for(int j=0; j<mtgFeatures.size(); ++j)
				{
					MTGNodeDataFeature feature = mtgFeatures.get(j);
					String featureJavaType = MTGKeys.codeToJavaTypeFeatureTypes(feature.getFeatureTypeIndex());
					if(featureJavaType==null)
						return null;
					String featureName = feature.getFeatureName();
					
					//declare attribute in module
					code = code + featureJavaType + " " + featureName + ";";
					
					//create set method
					code = code + "public void set" + featureName + "(" + featureJavaType + " val){this." + featureName + "=val;}";
					
					//create get method
					code = code + "public " + featureJavaType + " get" + featureName + "()" + "{return this." + featureName + ";}";
				}
				
				//close module code
				code = code + "}";
			}
			
			return XLFilter.compile(new StringReader(code),this.systemId);
			
			//			Type[] a = XLFilter.compile(new StringReader (
			//		            "import de.grogra.imp3d.objects.*;" +
			//		            "public class MyBox extends Box {" +
			//		            "public String toString() { return \"Foo \" + super.toString(); }" +
			//		            "}"), "Bar");
			//		    return a;
		}
		catch(Throwable t)
		{
			return null;
		}
	}
	*/
}

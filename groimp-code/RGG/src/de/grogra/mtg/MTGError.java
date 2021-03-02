/*
 * Copyright (C) 2011 Abteilung Oekoinformatik, Biometrie und Waldwachstum, 
 * Buesgeninstitut, Georg-August-Universitaet Göttingen
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
package de.grogra.mtg;

/**
 * @author Ong Yongzhi
 * @since  2011-11-18
 */
public class MTGError {
	protected static final int MTG_TRANSLATOR_TRANSLATE_SUCCESSFUL 			= 0;
	protected static final int MTG_TRANSLATOR_ERROR_MTG_BUFFER_CHECK 		= 1;
	protected static final int MTG_TRANSLATOR_ERROR_MTG_BUFFER_CLOSE		= 2;
	protected static final int MTG_TRANSLATOR_ERROR_MTG_FILE_INCOMPLETE  	= 3;
	protected static final int MTG_TRANSLATOR_ERROR_MTG_FILE_SYNTAX_ERROR  	= 4;
	protected static final int MTG_TRANSLATOR_ERROR_MTG_BUILDER			  	= 5;
	protected static final int MTG_TRANSLATOR_TOPO_RELATION_REMOVE_ERROR  	= 6;
	protected static final int MTG_TRANSLATOR_UNEXPECTED_ERROR			  	= 7;
	protected static final int MTG_TRANSLATOR_TRANSLATE_HEADER_SUCCESSFUL 	= 8;
	protected static final int MTG_TRANSLATOR_ERROR_HEADER				 	= 9;
	
	protected static final int MTG_MODULE_WRITE_SUCCESSFUL							= 0;
	protected static final int MTG_MODULE_WRITE_ERROR_NO_ROOT_NODE					= 1;
	protected static final int MTG_MODULE_WRITE_ERROR_NO_CLASSES					= 2;
	protected static final int MTG_MODULE_WRITE_ERROR_UNRECOGNIZED_FEATURE_TYPE		= 3;
	protected static final int MTG_MODULE_WRITE_ERROR_INVALID_BUFFEREDWRITER		= 4;
	protected static final int MTG_MODULE_WRITE_ERROR								= -1;
	
	protected static final int MTG_MODULE_COMPILE_SUCCESSFUL						= 0;
	protected static final int MTG_MODULE_COMPILE_ERROR_INVALID_BUFFEREDREADER		= 1;
	protected static final int MTG_MODULE_COMPILE_ERROR_READ_FILE					= 2;
	protected static final int MTG_MODULE_COMPILE_ERROR_COMPILATION					= 3;
//	protected static final int MTG_MODULE_WRITE_ERROR_NO_ROOT_NODE					= 1;
//	protected static final int MTG_MODULE_WRITE_ERROR_NO_CLASSES					= 2;
//	protected static final int MTG_MODULE_WRITE_ERROR_UNRECOGNIZED_FEATURE_TYPE		= 3;
	protected static final int MTG_MODULE_COMPILE_ERROR								= -1;
	
	public static class MTGGraphBuildException extends Exception
	{
		String errorMsg;
		
		public MTGGraphBuildException()
		{
			super();
			errorMsg = "Error parsing MTG File.";
		}
		
		public MTGGraphBuildException(String msg)
		{
			super();
			this.errorMsg = msg;
		}
		
		public String getError()
		{
			return errorMsg;
		}
	}
	
	public static class MTGPlantFrameException extends Exception
	{
		String errorMsg;
		
		public MTGPlantFrameException()
		{
			super();
			errorMsg = "Error constructing Plant Frame.";
		}
		
		public MTGPlantFrameException(String msg)
		{
			super();
			this.errorMsg = msg;
		}
		
		public String getError()
		{
			return errorMsg;
		}
	}
	
	public static class MTGExportException extends Exception
	{
		String errorMsg;
		
		public MTGExportException()
		{
			super();
			errorMsg = "Error exporting MTG file.";
		}
		
		public MTGExportException(String msg)
		{
			super();
			this.errorMsg = msg;
		}
		
		public String getError()
		{
			return errorMsg;
		}
	}
}


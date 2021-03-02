package de.grogra.gpuflux.jocl;

import java.io.IOException;

public class JOCLSource {
	private String filename;
	private String source;
	private JOCLCPP cpp;
	
	public JOCLSource( String filename )
	{
		this.filename = filename;
	}
	
	public String getSource() throws IOException
	{
		if( source == null )
		{
			cpp = new JOCLCPP();
			
			source = cpp.preproceseSourceFile(filename);
		}
		
		return source;
	}
	
	public String dereferenceError(String error)
	{
		if( cpp == null )
			return error;
		else
			return cpp.dereferenceError(error);
	}
	
}

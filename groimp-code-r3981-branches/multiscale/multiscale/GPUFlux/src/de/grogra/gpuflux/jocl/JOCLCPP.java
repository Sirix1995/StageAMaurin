package de.grogra.gpuflux.jocl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JOCLCPP {
		
	private static final int PP_TEXT = 0;
	private static final int PP_COMMENT_BLOCK = 1;
	private static final int PP_COMMENT_LINE = 2;
	private static final int PP_STRING = 3;
	
	HashSet<String> includedFiles = new HashSet<String>();
	Vector<SourceLine> line2file = new Vector<SourceLine>();
	
	int pp_state = 0;

	private int pp_charsonline;

	private char last_char;

	private int pp_line;
	
	public String preproceseSourceFile(String fileName) throws IOException
	{
		includedFiles.clear();
		line2file.clear();
		pp_state = 0;
		pp_line = 1;
		pp_charsonline = 0;
		last_char = '\n';
		
		String path = fileName.substring(0, Math.max( 0 , fileName.lastIndexOf('/') + 1));
		
		return readSourceFile( fileName, path );
	}
		
	class SourceLine
	{
		SourceLine(){};
		SourceLine( String fileName, int line )
		{
			this.fileName = fileName;
			this.line = line;
		}
		
		String fileName;
		int line;
	};
	
	public SourceLine line2sourceline( int line )
	{
		if( line >= line2file.size() )
			return null;
		return line2file.get( line );
	}
	
	// dereference all include statements
	private String readSourceFile(String fileName, String path) throws IOException
    {	
		URL resource = getClass().getResource(fileName);
		
		if( resource == null )
			throw new FileNotFoundException( "File " + resource + " not found");
			
		if( includedFiles.contains(resource.getPath()) )
			return "";
		
		includedFiles.add(resource.getPath());
		
		String ppsource = new String();
		String source = readFile( resource );
		
		int line = 1;
		
		for(int i = 0 ; i < source.length(); i++ )
		{
			char c = source.charAt( i );
			
			switch(pp_state)
			{
			case PP_TEXT:
				if( c == '"')
					pp_state = PP_STRING;
				if( c == '\n')
					pp_charsonline = 0;
				if( c == '#')
				{
					if( pp_charsonline == 0 )
					{
						if( source.regionMatches(false, i, "#include", 0, 8) )
						{
							int startIndex = source.indexOf("\"", i);
							int endIndex = source.indexOf("\"", startIndex + 1);
							
							String includeName = source.substring(startIndex + 1, endIndex);
							
							try
							{
								ppsource += readSourceFile( path + includeName, path );
							}
							catch( FileNotFoundException e )
							{
								throw new IOException( fileName + "(" + line + "): Could not load " + path + includeName );
							}
							
							i = endIndex;
							pp_charsonline++;
							continue;
						}
					}
				}
				if( c == '/' && last_char == '/' )
					pp_state = PP_COMMENT_LINE;
				if( c == '*' && last_char == '/' )
				{
					pp_state = PP_COMMENT_BLOCK;
					pp_charsonline-=2;
				}
				
				if( !isWhitespace(c) )
					pp_charsonline++;
				
				break;
			case PP_COMMENT_BLOCK:
				if( c == '/' && last_char == '*' )
					pp_state = PP_TEXT;
				break;
			case PP_COMMENT_LINE:
				if( c == '\n' )
				{
					pp_charsonline = 0;
					pp_state = PP_TEXT;
				}
				break;
			case PP_STRING:
				if( c == '"')
					pp_state = PP_TEXT;
				if( c == '\n')
					pp_state = PP_STRING;
				break;
			};
			
			if( c == '\n' )
			{
				line2file.add(new SourceLine( fileName, line ));
				pp_line++;
				line++;
			}
			
			ppsource += c;
			last_char = c;
		}
	
		return ppsource;
    }
	
	private boolean isWhitespace(char charAt) {
		return charAt == '\n' || charAt == '\r' || charAt == '\t' || charAt == ' ';
	}

	private String readFile(URL resource) throws IOException
    {	
		//InputStream stream = this.getClass().getResourceAsStream(fileName);
			
		InputStream stream = resource.openStream();
		
		//FileInputStream file = new FileInputStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while (true)
        {
            line = br.readLine();
            if (line == null)
            {
                break;
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
	
	
	public String dereferenceError( String message )	{
		message = dereferenceError( message, "\\(\\d*\\):" , ')' );
		message = dereferenceError( message, "\\:\\d*\\:" , ':' );
		return message;
	}
	
	public String dereferenceError(String message, String patern, char separator) {
		Pattern pattern = Pattern.compile( patern );
				
		Matcher matcher = pattern.matcher(message);
		
		String newMessage = new String();
		
		// match all occurrences of a source line number
		// for each match, add the source file name and line number of the original source file
		int finger = 0;
		while( matcher.find() )
		{
			String sequence = matcher.group();
			
			String submessage = message.substring(finger, matcher.end() - sequence.length() );
			
			String line = sequence.substring(1, sequence.indexOf(separator,1));
			
			int linenr = Integer.parseInt(line);
			
			SourceLine sourceline = line2sourceline(linenr);
			
			newMessage += submessage + sequence;
			
			if( sourceline != null )
				newMessage += sourceline.fileName + "(" + (sourceline.line - 1) + "):";
						
			finger = matcher.end();
		};
		
		newMessage += message.substring(finger);
		
		return newMessage;
		
	}
	
}

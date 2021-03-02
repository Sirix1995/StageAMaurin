package de.grogra.gpuflux.utils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FluxDebugger {
	private static final String FLUX_DEBUG_FILE = "fluxDebug.txt";
	private static FluxDebugger debugger;
	private BufferedWriter writer;
	
	private FluxDebugger(){
		try {
			writer = new BufferedWriter( new FileWriter(FLUX_DEBUG_FILE,false) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedWriter writer() { return writer; }
	
	public static FluxDebugger getDebugger()
	{ 
		if( debugger == null )
			debugger = new FluxDebugger();
		return debugger; 
	};

	public static void write(String string) {
		try {
			getDebugger().writer().write( string );
			getDebugger().writer().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};
	
	public void finalize() throws Throwable
	{
		writer.close();
		super.finalize();
	}

	public static void writeln(String string) {
		write( string + "\n");
	}
	
	public static void runGC ()
	{
		for( int i = 0 ; i < 4 ; i++ )
		{
			Runtime.getRuntime().runFinalization ();
			Runtime.getRuntime().gc ();
			try {
				Thread.sleep (250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static long usedMemory ()
    {
        return Runtime.getRuntime().totalMemory () - Runtime.getRuntime().freeMemory ();
    }

		
}

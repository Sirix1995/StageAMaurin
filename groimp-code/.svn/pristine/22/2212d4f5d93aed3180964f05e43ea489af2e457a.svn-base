package de.grogra.imp3d.glsl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;

import de.grogra.imp.ViewComponent;
import de.grogra.imp3d.RenderState;

/**
 * Measures is a utility that may store and retrieve 
 * time measures for {@link de.grogra.imp3d.gl.GLDisplay#repaint(int)}
 * and {@link GLSLDisplay#render(int)}. This class is a singleton
 * and will only produce correct results if not more than one Display-Window
 * is open. The methods may be called within the XL-Console. 
 * @author Konni Hartmann
 */
public class Measures {

	private Measures() {
		super();
	}
	
	long passDiffs[][] = new long[7][1000];
	
	/**
	 * Stores the time used to process a renderpass.
	 * @param rpID The ID of the renderpass. Renderpasses are arbitrarily 
	 * categorized into groups ranging from 0 to 6.
	 * @param value The time to be stored.
	 */
	public void setRPTimeDiff(int rpID, long value) {
		if(rpID < 0 || rpID > 6 || counter < 0 || counter > 999)
			return;
		passDiffs[rpID][counter] = value;
	}
	
	/**
	 * Print a summary of collected renderpass time.
	 */
	public void printRPSummary() {
		long avg[] = {0,0,0,0,0,0,0};
		
		for(int j = 0; j < 7; j++)
		for(int i = 0; i < 1000; i++) 
				avg[j] += passDiffs[j][i];
		long sum = 0;
		
		for(int j = 0; j < 7; j++) {
			avg[j] /= 1000;
			sum += avg[j];
		}
		System.out.println("Cache\tDepth\tLight\tBack\tTone\tTools\tPresent\tSum");
		for(int j = 0; j < 7; j++)
			System.out.printf("%.2f\t", (avg[j]/1000000.f));
//			System.out.print(avg[j]+"\t");
		System.out.println(sum);
	}
	
	long timeDiffs[] = new long[1000];
	
	void setTimeDiff(long value) {
		timeDiffs[counter] = value;
	}
	
	String currentFileName;
	
	/**
	 * Sets the name for a file into which measures may be saved.
	 * This will be used as a filename only. No directory or ending 
	 * should be added.
	 * @param s The filename.
	 */
	public void setCurrentFileName(String s) {
		currentFileName = s;
	}
	
	/**
	 * Print a summary of collected render times.
	 */
	public void printSummary() {
		long avg = timeDiffs[0];
		long min = timeDiffs[0];
		long max = timeDiffs[0];
		long cur;
		
		for(int i = 1; i < 1000; i++) {
			cur = timeDiffs[i];
			avg += cur;
			min = cur < min ? cur : min;
			max = cur > max ? cur : max;
		}
		avg /= 1000;
		System.out.println("Average:\tMin:\t\tMax:\t\t~FPS");
		System.out.printf("%.2f\t\t%.2f\t\t%.2f\t\t%.2f\n", (avg/1000000.f), (min/1000000.f), (max/1000000.f), (1000000000.f/avg));
		System.out.println("All: "+all+"\tRend: "+rend);
	}
	
	boolean redraw = false;
	
	/**
	 * Changes behavior of measuring method.
	 * @param redraw If set to true, the scene will be redrawn after 
	 * each call of the render method resulting in continuous drawing. 
	 */
	public void setRedraw(boolean redraw) {
		this.redraw = redraw;
	}
	
	/**
	 * Check if scene should be redrawn. This is used by the Displays 
	 * render-method to test if it should issue the (protected)
	 * {@link ViewComponent#repaint(int)} method.
	 * @return true if scene should be redrawn.
	 */
	public boolean shouldRedraw() {
		if(counter >=0 && counter < 1000)
			return redraw;
		return false;
	}
	
	/**
	 * Store the rendertimes obtained from the used Display class to 
	 * a csv-File in the current working directory. The name is specified
	 * by the method {@link #setCurrentFileName(String)}.
	 */
	public void saveTimeDiffSummary() {
	    try{
	        // Create file 
	        FileWriter fstream = new FileWriter(currentFileName+".csv");
	            BufferedWriter out = new BufferedWriter(fstream);
	          out.write(""+timeDiffs[0]);
	        for(int i = 1; i< 1000; i++)
	            out.write(";"+timeDiffs[i]);
	        //Close the output stream
	        out.close();
	        }catch (Exception e){//Catch exception if any
	          System.err.println("Error: " + e.getMessage());
	        }
	}
	
	static Measures _inst = null;
	
	/**
	 * @return An instance of this class. Since this class is a
	 * singleton the same reference will be returned for all calls
	 * of this method.
	 */
	public static Measures getInstance() {
		if(_inst == null)
			_inst = new Measures();
		return _inst;
	}

	/**
	 * This will reset the measurement.
	 */
	public void restartNow() {
		counter = -5;
	}
		
	long deltaTime = 0;

	int counter = 1001;
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	int all, rend;
	
	public void setData(int all, int rend) {
		this.all = all; this.rend = rend;
	}
	
	public void stopTimer(RenderState rs) {
		if (counter < 0) {
			System.out.println("Starting in "+(-counter));
			counter++;
		}
		if (counter < 1000 && counter >= 0) {
			Measures.getInstance().setTimeDiff(System.nanoTime()-deltaTime);
			if(counter%100 == 50)
				System.out.print(""+(counter/100));
			counter++;				
		}
		if (counter == 1000) {
			System.out.println();
			Measures.getInstance().printSummary();
			Calendar calendar = Calendar.getInstance();

			// get a java.util.Date from the Calendar instance.
			// this date will represent the current instant, or "now".
			java.util.Date now = calendar.getTime();
			
			// create a JDBC Timestamp instance			
			Measures.getInstance().setCurrentFileName(rs.getClass().getSimpleName()+now.getDate()+"-"+(now.getMonth()+1)+"-"+(now.getYear()+1900)+"_"+now.getHours()+"_"+now.getMinutes()+"_"+now.getSeconds());
			counter++;
		}
	}

	public void startTimer() {
		deltaTime = System.nanoTime();
	}
}


/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.ext.openalea;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import sun.security.action.GetPropertyAction;
import de.grogra.ext.exchangegraph.IOContext;
import de.grogra.ext.exchangegraph.XEGExport;
import de.grogra.ext.exchangegraph.XEGImport;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Workbench;
import de.grogra.reflect.Method;
import de.grogra.reflect.Type;
import de.grogra.rgg.RGGRoot;
import de.grogra.rgg.model.Runtime;
import de.grogra.vfs.MemoryFileSystem;

public class OpenAleaHttpHandler implements HttpRequestHandler {

	static class MethodInvocation {
		public int count;
		public String method;
		public MethodInvocation(String method, int count) {
			this.count = count;
			this.method = method;
		}
	}
	
	@SuppressWarnings("serial")
	private class HandleException extends Exception {
		public HandleException(String s) {
			super(s);
		}
	}
	
	static final String XLCODE = "xlcode";
	static final String GRAPH = "graph";
	static final String RUNMETHOD = "command";
	static String MODEL_NAME = "RemoteModel";
	
	@SuppressWarnings("unchecked")
	static Type compiledModelType = null;
	static Object compiledModel = null;

	private final Context ctx;
	
	public OpenAleaHttpHandler(Context ctx) {
		this.ctx = ctx;
	}

	@SuppressWarnings("unchecked")
	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		Map<String, String> requestData = new HashMap<String, String>();
		String result = "Unknown error occured";
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		
		try {
		
			// check that method was POST
			String method = request.getRequestLine().getMethod().toUpperCase(
					Locale.ENGLISH);
			if (!method.equals("POST")) {
				throw new HandleException("Http form action method was not POST");
			}
	
			// try to obtain the HTTP entity
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				String content = null;
				try {
					content = EntityUtils.toString(entity);
				} catch (ParseException e) {
					throw new HandleException("ParseException: " + e.getMessage());
				} catch (IOException e) {
					throw new HandleException("IOException: " + e.getMessage());
				}
				Header contentType = entity.getContentType();
	
				// check that content type is application/x-www-form-urlencoded
				if ("application/x-www-form-urlencoded".equals(contentType
						.getValue())) {
					requestData.clear();
					// split content at every = or & to extract key/value pairs from
					// content
					String[] s = content.split("[=&]");
					String encodingScheme = (String)AccessController.doPrivileged(
							new GetPropertyAction("file.encoding"));
					for (int i = 0; i < s.length; i += 2) {
						String key = s[i];
						String value = null;
						
						if (i+1 < s.length){
							value = s[i + 1];
							value = URLDecoder.decode(value, encodingScheme); 
						}
						// System.err.println(key + " / " + value);
						if (value!= null && value.equals("None")){
							value = null;
						}
						requestData.put(key, value);
					}
				}
			}
	
			// find groimp graph
			Workbench wb = ctx.getWorkbench();
			Registry r = wb.getRegistry();
			
		    MemoryFileSystem fs = (MemoryFileSystem) r.getFileSystem();
		    Object[] list = fs.listFiles(fs.getRoot());  

			
			GraphManager graph = r.getProjectGraph();
	
			String xlCode = requestData.get(XLCODE);			
			
			if (xlCode == null){
		    	try {
		    		// for local model, we get the model from registry (See getGroimpNodeTypes() method in MappleTXEGImport class.), not by compling xl code,
		    		// local model may contains several files, but here we get only xlcode from one file to use it as flag
					xlCode = list[0].toString();
					// local model has its own name, the model_name here is just used as a flag
					MODEL_NAME = "LocalModel";
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new Exception("No local GroIMP model are aviable !");
				}
			}
			
			String graphCode = requestData.get(GRAPH);
			String command = requestData.get(RUNMETHOD);
			
			//TODO: zlib-decompression of graph/xlcode
			// doesn't work yet
//			Inflater decompressor = new Inflater(true);
//			decompressor.setInput(graphCode.getBytes());
//			
//			byte[] resultzip = new byte[10000];
//			int resultLength = 0;
			
//			StringBufferInputStream sbis = new StringBufferInputStream(graphCode);
//			try {
//				ZipInputStream gzipis = new ZipInputStream(sbis);
//				gzipis.read(resultzip);
//			}
//			catch (IOException e) {
//				System.err.println(e);
//			}
						
//			try {
//				resultLength = decompressor.inflate(resultzip);
//			} catch (DataFormatException e2) {
//				e2.printStackTrace();
//			}
//			decompressor.end();
//			graphCode = new String(resultzip);
			
			// create list of run methods. We assume the semicolon (i.e. ;) was used within the incoming command-string as command splitter
			command = command.replaceAll("\\s","");
			String[] commands = command.split(";");
			ArrayList<MethodInvocation> runMethods = new ArrayList<MethodInvocation>();
			for (String c : commands) {
				StringBuffer countString = new StringBuffer();
				int count = 1;
				int index = 0;
								
				while ((c.charAt(index) >= '0') && (c.charAt(index) <= '9')) {
					countString.append(c.charAt(index));
					index++;
				}
				
				if (countString.length() > 0)
					count = Integer.valueOf(countString.toString());
				
				runMethods.add(new MethodInvocation(c.substring(index), count));
			}
			
			// prepare root structure for the graph
			Node root = graph.getRoot();
			root.removeAll(null);
			RGGRoot rggRoot = new RGGRoot();
			root.addEdgeBitsTo(rggRoot, Graph.BRANCH_EDGE, null);
			
			// import scene graph from given string
			IOContext ctx = new IOContext();
			XEGImport importer = new XEGImport(new StringReader(graphCode), rggRoot, ctx, xlCode, MODEL_NAME);
			
			try {
				importer.doImport();
			}
			catch (IOException e) {
				throw new HandleException("IOException: " + e.getMessage());
			}
			
			// set graph to runtime, so that xl-compiler knows what graph to use
			Runtime.INSTANCE.setCurrentGraph(graph);
			Type[] types = importer.getCompiledTypes();
			
			for (Type type : types) {
				int iend = type.getName().indexOf(".");
				String subString = null;
				if (iend != -1) {
					subString = type.getName().substring(0 , iend); 
				}
				
				if (subString == null){
					compiledModelType = type;
				}
			}
			
			
			// create object of xl code model
			try {
				compiledModel = compiledModelType.newInstance();
			} catch (InvocationTargetException e1) {
				throw new HandleException("InvocationTargetException: " + e1.getMessage());
			} catch (InstantiationException e1) {
				throw new HandleException("InstantiationException: " + e1.getMessage());
			} catch (IllegalAccessException e1) {
				throw new HandleException("IllegalAccessException: " + e1.getMessage());
			}
			
			Runtime.INSTANCE.currentGraph().derive();

			// loop over methods to run
			for (MethodInvocation runMethod : runMethods) {
				Method m;
				Type t;
				String run;
				
				// if runMethod == init-Method use invokeInit instead
				if (runMethod.method.equals("init")) {
					run = "invokeInit";
					t = compiledModelType.getSupertype();
				}
				else {
					run = runMethod.method;
					t = compiledModelType;
				}
				
				
				// find run method
				Method m_run = null;
				for (int i = 0; i < t.getDeclaredMethodCount(); i++) {
					m = t.getDeclaredMethod(i);
					if (m.getName().equals(run)
							&& (m.getParameterCount() == 0)) {
						m_run = m;
						break;
					}
				}
				
				if (m_run == null) {
					throw new HandleException("Method " + run + " could not be found");
				}
	
				// run run method
				try {
					for (int i = 0; i < runMethod.count; i++) {
						m_run.invoke(compiledModel, null);
						Runtime.INSTANCE.currentGraph().derive();
					}
				} catch (InvocationTargetException e) {
					throw new HandleException("InvocationTargetException: " + e.getMessage());
				} catch (IllegalAccessException e) {
					throw new HandleException("IllegalAccessException: " + e.getMessage());
				}
			}
	
			// export modified graph
			XEGExport exporter = new XEGExport(rggRoot, ctx);
			String graphString = exporter.doExport();
			
			// construct result
			result = graphString;			
			response.setStatusCode(HttpStatus.SC_OK);
			
		} catch (HandleException e) {
			// handle exception
			result = "An error occured!\n\n" + e.getMessage();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// create result for http response
			StringEntity body;
			try {
				body = new StringEntity(result);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}

			body.setContentType("text/plain");
			response.setEntity(body);
		}
		
	}

}


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
import java.net.Socket;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.util.Lock;

public class OpenAleaCommand extends LockProtectedCommand {

	final GraphManager graph;

	public OpenAleaCommand(GraphManager graph) {
		super(graph, true, JobManager.ACTION_FLAGS);
		this.graph = graph;
	}

	@Override
	protected void runImpl(Object info, Context ctx, Lock lock) {
		
		HttpData data = (HttpData) info;
		Socket socket = data.getSocket();
		HttpParams params = data.getParams();
		
		DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
//		System.out.println("Incoming connection from "
//				+ socket.getInetAddress());
		try {
			conn.bind(socket, params);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set up the HTTP protocol processor
		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		httpproc.addInterceptor(new ResponseDate());
		httpproc.addInterceptor(new ResponseServer());
		httpproc.addInterceptor(new ResponseContent());
		httpproc.addInterceptor(new ResponseConnControl());

		// Set up request handlers
		HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
		registry.register("*", new OpenAleaHttpHandler(ctx));

		// Set up the HTTP service
		HttpService httpService = new HttpService(httpproc,
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory());
		httpService.setParams(params);
		httpService.setHandlerResolver(registry);

		// do work of worker thread here
		HttpContext context = new BasicHttpContext(null);
		try {
			httpService.handleRequest(conn, context);
		} catch (ConnectionClosedException ex) {
			System.err.println("Client closed connection");
		} catch (IOException ex) {
			System.err.println("I/O error: " + ex.getMessage());
		} catch (HttpException ex) {
			System.err.println("Unrecoverable HTTP protocol violation: "
					+ ex.getMessage());
		} finally {
			try {
				conn.shutdown();
			} catch (IOException ex) {
				System.err.println("Connection shutdown problem: " + ex.getMessage());
			}
		}
	}
	
}

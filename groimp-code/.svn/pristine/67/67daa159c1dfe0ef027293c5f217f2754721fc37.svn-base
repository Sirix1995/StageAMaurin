
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
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Workbench;

public class RequestListenerThread extends Thread {

	private final ServerSocket serversocket;
	private final HttpParams params;
	private final Context ctx;

	public RequestListenerThread(Context ctx, int port)
			throws IOException {
		this.serversocket = new ServerSocket(port);
		this.params = new BasicHttpParams();
		this.params
				// .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
				.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
						8 * 1024).setBooleanParameter(
						CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
				.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
				.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
						"HttpComponents/1.1");
		this.ctx = ctx;
	}

	public void run() {
		System.out.println("Listening on port "
				+ this.serversocket.getLocalPort());
		while (!Thread.interrupted()) {
			try {
				// Set up HTTP connection
				Socket socket = this.serversocket.accept();
				
				
				// find graph
				Workbench wb = ctx.getWorkbench();
				Registry r = wb.getRegistry();
				GraphManager graph = r.getProjectGraph(); 
				
				// fill data for command
				HttpData data = new HttpData(socket, params);
				
				// create command and send to jobmanager
				Command c = new OpenAleaCommand(graph);
				ctx.getWorkbench().getJobManager().runLater(c, data, ctx, JobManager.ACTION_FLAGS);
				
			} catch (InterruptedIOException ex) {
				break;
			} catch (IOException e) {
				System.err.println("I/O error initialising connection thread: "
						+ e.getMessage());
				break;
			}
		}
	}
}

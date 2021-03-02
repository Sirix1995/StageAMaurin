
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
import de.grogra.pf.ui.Context;

public class Server {
	
	private RequestListenerThread t = null;

	public Server (Context ctx, int port) {
		try {
			t = new RequestListenerThread(ctx, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		t.setDaemon(false);
		t.start();		
	}
	
	public boolean isServerReady() {
		return t != null;
	}

}


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

package de.grogra.ray2;

/**
 * This interface is used by a {@link de.grogra.ray2.Renderer} to monitor
 * the progress of rendering and to show messages.
 * 
 * @author Ole Kniemeyer
 */
public interface ProgressMonitor
{
	/**
	 * Value for {@link #setProgress} indicating that the current
	 * state of progress is indetermined.
	 */
	float INDETERMINATE_PROGRESS = -1;

	/**
	 * Value for {@link #setProgress} indicating that the renderer
	 * has done its job.
	 */
	float DONE_PROGRESS = 2;

	/**
	 * This method is invoked by the renderer to monitor its progress. 
	 * 
	 * @param text short text to display
	 * @param progress state of progress from 0 to 1, or one of the
	 * constants {@link #INDETERMINATE_PROGRESS}, {@link #DONE_PROGRESS}
	 */
	void setProgress (String text, float progress);

	/**
	 * This method is invoked by the renderer to show a message, e.g.,
	 * the statistics after rendering has completed.
	 * 
	 * @param message message to display
	 */
	void showMessage (String message);
}

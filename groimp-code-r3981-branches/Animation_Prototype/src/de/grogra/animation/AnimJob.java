package de.grogra.animation;

import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.util.Lockable;

public abstract class AnimJob extends LockProtectedCommand {

	private final Context ctx;
	private final int flags;
	private final Object info;
	protected final GraphManager graph;

	private boolean started = false;
	private boolean done = false;

	private AnimJob (Lockable resource, boolean write, int flags, Context ctx, Object info) {
		super (resource, write, flags);
		this.ctx = ctx;
		this.flags = flags;
		this.info = info;
		this.graph = ctx.getWorkbench ().getRegistry ().getProjectGraph ();
	}

	public AnimJob (Object info, Context ctx) {
		this (ctx.getWorkbench ().getRegistry ().getProjectGraph (), true, JobManager.ACTION_FLAGS, ctx, info);
	}
	
	public void execute() {
		if (started) {
			throw new IllegalStateException (this + " may only be executed once");
		}
		started = true;
		JobManager jm = ctx.getWorkbench ().getJobManager ();
		jm.runLater (this, info, ctx, flags);
	}

	@Override
	protected void done (Context c) {
		if (done) {
			return;
		}
		done = true;
		synchronized (this) {
			notifyAll();
		}
	}
	
	public boolean isDone() {
		return done;
	}

}

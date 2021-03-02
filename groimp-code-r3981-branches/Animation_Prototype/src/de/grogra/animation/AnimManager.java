package de.grogra.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EventObject;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JToggleButton;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.handler.animationnodes.AnimationNodesHandler;
import de.grogra.animation.handler.sgcloning.SGCloningHandler;
import de.grogra.animation.handler.sgnodereference.SGNodeReferenceHandler;
import de.grogra.animation.handler.simpleproperty.SimplePropertyHandler;
import de.grogra.animation.timeline.TimelineControls;
import de.grogra.animation.timeline.TimelineManager;
import de.grogra.animation.trackview.TrackViewManager;
import de.grogra.animation.util.Debug;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.GraphTransaction;
import de.grogra.graph.impl.Node;
import de.grogra.imp.IMPWorkbench;
import de.grogra.persistence.IndirectField;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.persistence.XAListener;
import de.grogra.persistence.Transaction.Data;
import de.grogra.persistence.Transaction.Reader;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.GraphSelection;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.reflect.TypeId;
import de.grogra.util.EventListener;
import de.grogra.vfs.FileSystem;

public class AnimManager extends AnimConsumer implements EventListener, ActionListener, XAListener {

	final private GraphManager graph;
	final private Workbench wb;
	private Handler valueHandler;
	final private TimelineManager timelineManager;
	final private TrackViewManager trackViewManager;
	private Transaction.Reader reader;
	public boolean updating;
	private Set<Integer> keys;
	
	public boolean hasUpdateJob;
	
	public AnimManager(GraphManager graph, Workbench wb) {
//		valueHandler = new SimplePropertyHandler(graph, wb);
//		valueHandler = new SGCloningHandler(graph, wb);
//		valueHandler = new AnimationNodesHandler(graph, wb);
		valueHandler = new SGNodeReferenceHandler(graph, wb);
		UIProperty.WORKBENCH_SELECTION.addPropertyListener(wb, this);
		reader = new GraphTransaction(graph, null).createReader();
		updating = false;
		keys = new TreeSet<Integer>();
		this.graph = graph;
		this.wb = wb;
		
		this.timelineManager = new TimelineManager(wb);
		TimelineControls timelineControls = timelineManager.getTimeline().getTimelineControls(); 
		timelineControls.getAutoKeyButton().addActionListener(this);
		timelineControls.getClearAnimationButton().addActionListener(this);
		
		//TODO: not a good design to submit the own object
		this.trackViewManager = new TrackViewManager(wb, graph, this, timelineManager.getTimeContext());
		
		hasUpdateJob = false;
	}
	
	public void putValue(int time, Object node, PersistenceField property, Object value) {
		boolean keyExisted = valueHandler.putValue(time, node, property, value);
		if (!keyExisted) {
			// create ticks at keyframeslider
			Object s = UIProperty.WORKBENCH_SELECTION.getValue (wb);
			if ((s instanceof GraphSelection) &&
					((GraphSelection) s).contains (graph, node, true)) {
				timelineManager.addKey(time);
				timelineManager.updateKeys();
			}
		}
		trackViewManager.getTrackView().refresh();
	}
	
	/**
	 * Change a already save value in the value handler.
	 * Inform GroIMP about a new value.
	 */
	public void changeValue(int oldTime, int newTime, Object node, PersistenceField property, Object value) {
		valueHandler.changeValue(oldTime, newTime, node, property, value);
		
		// modifiy ticks at keyframeslider
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (wb);
		if ((s instanceof GraphSelection) &&
				((GraphSelection) s).contains (graph, node, true)) {
			timelineManager.removeKey(oldTime);
			timelineManager.addKey(newTime);
			timelineManager.updateKeys();
		}
		trackViewManager.getTrackView().refresh();
		// update GroIMP
		updateAnimValues(this.getCurrentTime(), false);
	}
	
	public Object getValue(double time, Object node, PersistenceField property) {
		return valueHandler.getValue(time, node, property);
	}
	
	public void addEdgeBits(int time, Object source, Object target, int mask) {
		valueHandler.addEdgeBits(time, source, target, mask);
	}
	
	public void removeEdgeBits(int time, Object source, Object target, int mask) {
		valueHandler.removeEdgeBits(time, source, target, mask);
	}
	
	public void makeTransient(int time, Object node, ManageableType type) {
		valueHandler.makeTransient(time, node, type);
	}

	public void clearAnimation() {
		timelineManager.deleteKeys();
		trackViewManager.getTrackView().refresh();
		valueHandler.clearValues();
	}
	
	public void updateAnimValues(final int time, boolean fromJob) {
		Debug.println("AnimManager: updateAnimValues");
		timelineManager.setCurrentTime(time);
		trackViewManager.getTrackView().redraw();
		if (fromJob) {
			updating = true;
			valueHandler.update(time);
			updating = false;
		}
		else {
			synchronized (this) {
				if (hasUpdateJob) {
					Debug.println("Update job canceled");
					return;
				}
				hasUpdateJob = true;
				AnimJob job = new AnimJob(null, wb) {
					@Override
					protected void runImpl(Object arg, Context ctx) {
						AnimManager.this.updating = true;
						valueHandler.update(time);
						AnimManager.this.updating = false;
						AnimManager.this.hasUpdateJob = false;
					}
				};
				job.execute();
			}
		}
	}

	public TimelineManager getTimelineManager() {
		return timelineManager;
	}
	
	public TrackViewManager getTrackViewManager() {
		return trackViewManager;
	}

	public int getCurrentTime() {
		return timelineManager.getCurrentTime();
	}
	
	public void setAnimationRange(int start, int end) {
		timelineManager.setTimelineRange(start, end);
	}
	
	public boolean saveAnimationData(FileSystem fs) {
		boolean result = false;
		updating = true;
		result = valueHandler.saveData(fs, timelineManager.getTimeContext().getCurrentTime());
		updating = false;
		return result;
	}
	
	public boolean restoreAnimationData(FileSystem fs) {
		boolean result = false;
		updating = true;
		result = valueHandler.restoreData(fs);
		updating = false;
		return result;
	}
	
	/**
	 * Called when object selection changes.
	 */
	public void eventOccured(EventObject event) {
		if (event instanceof UIPropertyEditEvent) {
			UIPropertyEditEvent uipee = (UIPropertyEditEvent) event;
			if (uipee.getNewValue() instanceof GraphSelection) {
				GraphSelection gs = (GraphSelection) uipee.getNewValue();
				int selectCount = gs.size();
		
				// collect keyframes from all selected objects
				keys.clear();
				for (int i = 0; i < selectCount; i++) {
					Object o = gs.getObject(i);
					valueHandler.getTimes(o, keys);
				}
				
				timelineManager.setKeys(keys);
				timelineManager.updateKeys();
			}
			else if ((uipee.getOldValue() instanceof GraphSelection)
					&& (uipee.getNewValue() == null)) {
				timelineManager.deleteKeys();
			}
		}
	}

	public void transactionApplied(Data xa, boolean rollback) {
		Debug.println("AnimManager: transactionApplied, updating: " + updating);
		if (updating)
			return;
		try {
			reader.getQueue ().restore (xa);
			reader.resetCursor ();
			reader.supply(this);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void begin() {
		Debug.println("AnimConsumer: begin, updating: " + updating);
		//TODO: erkennen ob XL-Ausfuehrung, dann Zeit hochsetzen
		
	}

	public void end() {
		Debug.println("AnimConsumer: end");

	}

	public void insertComponent(PersistenceCapable o, PersistenceField field,
			int[] indices, Reader reader) {
		Debug.println("AnimConsumer: insertComponent");
		
	}

	public void makePersistent(long id, ManageableType type) {
		Debug.println("AnimConsumer: makePersistent");
		
	}

	public void makeTransient(PersistenceCapable o, ManageableType type) {
		Debug.println("AnimConsumer: makeTransient");
		makeTransient(getCurrentTime(), o, type);
	}

	public void readData(PersistenceCapable o, Reader reader) {
		Debug.println("AnimConsumer: readData");
		
	}

	public void removeComponent(PersistenceCapable o, PersistenceField field,
			int[] indices, Reader reader) {
		Debug.println("AnimConsumer: removeComponent");
		
	}

	public void setField(PersistenceCapable o, PersistenceField field,
			int[] indices, Reader reader, boolean inverse) {
		Debug.println("AnimConsumer: setField, updating: " + updating);
		Object oldValue = null;
		Object value = null;
		switch (field.getType().getTypeId()) {
			case TypeId.BOOLEAN: {
				oldValue = reader.readBoolean ();
				value = reader.readBoolean ();
				break;
			}
			case TypeId.BYTE: {
				oldValue = reader.readByte ();
				value = reader.readByte ();
				break;
			}
			case TypeId.SHORT: {
				oldValue = reader.readShort ();
				value = reader.readShort ();
				break;
			}
			case TypeId.CHAR: {
				oldValue = reader.readChar ();
				value = reader.readChar ();
				break;
			}
			case TypeId.INT: {
				oldValue = reader.readInt ();
				value = reader.readInt ();
				break;
			}
			case TypeId.LONG: {
				oldValue = reader.readLong ();
				value = reader.readLong ();
				break;
			}
			case TypeId.FLOAT: {
				oldValue = reader.readFloat ();
				value = reader.readFloat ();
				break;
			}
			case TypeId.DOUBLE: {
				oldValue = reader.readDouble ();
				value = reader.readDouble ();
				break;
			}
			case TypeId.OBJECT: {
				try {
					oldValue = ManageableType.read(field.getLastField(), reader);
					value = ManageableType.read(field.getLastField(), reader);
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		
		if (value != null) {
			IndirectField newField = new IndirectField(field);
			if (oldValue != null) {
				//set key for old value if no key yet exists
				valueHandler.getTimes(o, keys);
				if (keys.isEmpty()) {
					putValue(timelineManager.getTimeContext().getStart(), o, newField, oldValue);
				}
			}
			// set key for new value
			Debug.println("o: " + o + ", newField: " + newField + ", value: " + value + ", oldValue: " + oldValue);
			putValue(getCurrentTime(), o, newField, value);
		}
	}

	public void addEdgeBits(Node source, Node target, int mask) {
		Debug.println("AnimConsumer: addEdgeBits");
		addEdgeBits(getCurrentTime(), source, target, mask);
	}

	public void removeEdgeBits(Node source, Node target, int mask) {
		Debug.println("AnimConsumer: removeEdgeBits");
		removeEdgeBits(getCurrentTime(), source, target, mask);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(timelineManager.getTimeline().getTimelineControls().getAutoKeyButton())) {
			JToggleButton autoKeyButton = timelineManager.getTimeline().getTimelineControls().getAutoKeyButton();
			if (autoKeyButton.getModel().isSelected()) {
				graph.addXAListener(this);
			}
			else {
				graph.removeXAListener(this);
			}
		}
		else if (source.equals(timelineManager.getTimeline().getTimelineControls().getClearAnimationButton())) {
			clearAnimation();
		}
	}
	
	public static void animationStep (IMPWorkbench workbench, Context ctx, String methodName) {
		System.err.println("animationStep, methodName: "+ methodName);
		AnimCore animCore = (AnimCore) workbench.getProperty(Init.ANIMCORE);
		if ((methodName != null) && (animCore.getAnimManager().getTimelineManager().getTimeContext().isStepOnExecution()))
			Library.timestep();
	}

	public Handler getValueHandler() {
		return valueHandler;
	}

}

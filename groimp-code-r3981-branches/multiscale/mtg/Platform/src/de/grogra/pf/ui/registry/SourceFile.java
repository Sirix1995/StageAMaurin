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

package de.grogra.pf.ui.registry;

import java.io.File;
import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.icon.IconSource;
import de.grogra.persistence.ManageableType;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.MimeTypeItem;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ProgressMonitor;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.io.ResourceLoader;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.registry.TypeItem;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Showable;
import de.grogra.pf.ui.TextEditor;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.reflect.Method;
import de.grogra.reflect.Type;
import de.grogra.util.Lock;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.xl.lang.ObjectToBoolean;

public class SourceFile extends Item implements
		de.grogra.pf.ui.registry.UIItem, ObjectToBoolean,
		TreeModelListener, Showable
{

	public static class Loader extends FilterBase implements ObjectSource,
			ProjectLoader
	{
		public Loader (FilterItem item, FilterSource source)
		{
			super (item, source);
			setFlavor (IOFlavor.PROJECT_LOADER);
		}

		public Object getObject ()
		{
			return this;
		}

		public void loadRegistry (Registry r)
		{
			File f = ((FileSource) source).getInputFile ();
			r.initFileSystem (new LocalFileSystem (IO.PROJECT_FS, f
				.getParentFile ()));
			SourceFile sf = new SourceFile (IO.toSystemId (r.getFileSystem (),
				f), IO.getRoot (source).getFlavor ().getMimeType ());
			r.getDirectory ("/project/objects/files", null).addUserItem (sf);
			Workbench w = Workbench.get (r);
			if (w != null)
			{
				Item i = item.getItem ("layout");
				if (i != null)
				{
					i = i.resolveLink (r);
				}
				if (i != null)
				{
					w.setProperty (Workbench.INITIAL_LAYOUT, i.getAbsoluteName ());
					sf.showLater (w);
				}
			}
		}

		public void loadGraph (Registry r)
		{
			r.setEmptyGraph ();
		}
	}

	protected MimeType mimeType;
	//enh:field

	protected boolean editable = true;
	//enh:field getter
	
	protected boolean disabled = false;
	//enh:field getter setter

	protected transient String deactivationCategory;
	protected transient int activationStamp = -1;
	
	public int getActivationStamp()
	{
		return activationStamp;
	}
	
	public void setActivationStamp(int stamp)
	{
		this.activationStamp = stamp;
	}
	
	private SourceFile ()
	{
		this (null, null);
	}

	public SourceFile (String key, MimeType mimeType)
	{
		super (key);
		this.mimeType = mimeType;
	}

	public FileSource toFileSource ()
	{
		return FileSource.createFileSource (getName (), mimeType, this, new StringMap (this));
	}
	
	public MimeType getMimeType ()
	{
		return mimeType;
	}
	
	public MimeTypeItem getMimeTypeItem ()
	{
		return MimeTypeItem.get (this, mimeType);
	}
	
	private void reinstantiateMTG()
	{
		//begin from graph root node
		Node rootNode = getRegistry().getProjectGraph().getRoot();		
		
		//traverse graph and reinstantiate nodes
		if(rootNode!=null)
			reinstantiateMTGInternal(rootNode);
	}
	
	private boolean isObsoleteInstance(Node node)
	{		
		//if node is of a generated mtg module type
		if(isMTGNode(node))
		{
			//loop through lists of types in registry
			Item dir = getRegistry().getItem("/classes");
			for (Item c = (Item) dir.getBranch(); c != null; c = (Item) c.getSuccessor()) 
			{
				Type<?> t = (Type<?>) ((TypeItem) c).getObject();
				
				//the node's type has the same name as this type in the registry
				if(node.getNType().getName().equals(t.getName()))
				{
					//check if node is an instance of this type in the registry
					if(!t.isInstance(node))
					{
						//return true to say this node's type is obsolete.
						// i.e. modules in the generated xl file have been recompiled.
						//      this node's type is no longer recognized. to re-instantiate this node using new type.
						return true;
					}
					else
					{
						//the node's type matches what is found in the registry
						return false;
					}
				}
			}
		}
		return false;
	}
	
	private boolean isMTGNode(Node node)
	{
		NType nType = node.getNType();
		if(nType!=null)
		{
			//if(nType.getName().contains(MTGKeys.MTG_MODULE_PREFIX))
			if(nType.getName().contains("mtg_"))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	private Type getNewType(Node node)
	{
		//loop through lists of types in registry
		Item dir = getRegistry().getItem("/classes");
		for (Item c = (Item) dir.getBranch(); c != null; c = (Item) c.getSuccessor()) 
		{
			Type<?> t = (Type<?>) ((TypeItem) c).getObject();
			
			//the node's type has the same name as this type in the registry
			if(node.getNType().getName().equals(t.getName()))
				return t;
		}
		return null;
	}
	
	public static void copyNodeFields(Node oldNode, Node newNode)
	{
		//Field[] oldf = null;
		//Field[] newf = null;
		
		//Class<?> oldInstance = oldNode.getNType().getImplementationClass();
		//Class<?> newInstance = newNode.getNType().getImplementationClass();
		//oldf = oldInstance.getDeclaredFields();
		//newf = newInstance.getDeclaredFields();
		
		NType oldType = oldNode.getNType();
		NType newType = newNode.getNType();
		
		int methodCount = oldType.getDeclaredMethodCount();
		int newMethodCount = newType.getDeclaredMethodCount();
		
		for(int m=0; m<methodCount; ++m)
		{
			//Method method = types[k].getDeclaredMethod(m);
			Method method = oldType.getDeclaredMethod(m);
			
			//set value of attribute if setter method found
			if((method.getName()).startsWith("get"))
			{
				String variableName = method.getName();
				variableName = variableName.substring(3);
				
				//find corresponding set method in new type
				for(int i=0; i<newMethodCount; ++i)
				{
					Method newMethod = newType.getDeclaredMethod(i);
					
					//if corresponding set method in new type found
					if(newMethod.getName().equals("set"+variableName))
					{
						Object[] oldParameters = new Object[0];
						Object[] newParameters = new Object[1];
						
						try {
							//set value as parameter of set method of new type
							newParameters[0] = method.invoke(oldNode, oldParameters);
							//invoke set method of new type to set value into new node
							newMethod.invoke(newNode, newParameters);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
				}
			}
		}
		try {
			
			//newNode.getUserField(9).setDouble(newNode, oldNode.getUserField(9).getDouble(oldNode));
			for (int i = 0; i < newType.getManagedFieldCount (); i++)
			{
				ManageableType.Field mf = newType.getManagedField (i);
				
				//if field is standard MTG attribute
				if(isStandardAttribute(mf.getName()))
				{
				
					ManageableType.Field mfOld = null;
					
					//find same field in old type
					for(int j=0; j<oldType.getManagedFieldCount(); ++j)
					{
						mfOld = oldType.getManagedField(j);
						if(mfOld.getName().equals(mf.getName()))
						{
							if(isStandardAttributeDouble(mfOld.getName()))
								mf.setDouble(newNode, mfOld.getDouble(oldNode));
							if(isStandardAttributeInt(mfOld.getName()))
								mf.setInt(newNode, mfOld.getInt(oldNode));
							if(isStandardAttributeObject(mfOld.getName()))
								mf.setObject(newNode, mfOld.getObject(oldNode));
						}
					}
					
					
				}
				
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//MTGNode oldmtg = (MTGNode)oldNode;
		//MTGNode newmtg = (MTGNode)newNode;
		
		//MTGNode.copyStdAttributes(oldmtg, newmtg);
	}
	
	private static boolean isStandardAttribute(String featureName)
	{
		if(featureName==null)
			return false;
		
		if(
				isStandardAttributeDouble(featureName) ||
				isStandardAttributeInt(featureName) ||
				isStandardAttributeObject(featureName) 
				)
		{
			return true;
		}
		
		return false;
	}
	
	private static boolean isStandardAttributeDouble(String featureName)
	{
		if((featureName.equals("L1"))||
				(featureName.equals("L2"				))||
				(featureName.equals("L3"				))||
				(featureName.equals("DAB"				))||
				(featureName.equals("DAC"				))||
				(featureName.equals("DBC"				))||
				(featureName.equals("XX"				))||
				(featureName.equals("YY"				))||
				(featureName.equals("ZZ"				))||
				(featureName.equals("Length"))||
				(featureName.equals("Azimut"			))||
				(featureName.equals("Alpha"			))||
				(featureName.equals("AA"			))||	
				(featureName.equals("BB"				))||
				(featureName.equals("CC"				))||
				(featureName.equals("TopDia"			))||
				(featureName.equals("BotDia"		))||
				(featureName.equals("Position"		)))
				{
				return true;}
		
		return false;
	}
	
	private static boolean isStandardAttributeInt(String featureName)
	{
		if(
		(featureName.equals("Category"		))||
		(featureName.equals("Order"			)) ||
		(featureName.equals("mtgClassID"			)) ||
		(featureName.equals("mtgID"			)) ||
				(featureName.equals("mtgScale"			))||
				(featureName.equals("stdAttFlag"			))
				)
		{
			return true;
		}
		
		return false;
	}
	
	private static boolean isStandardAttributeObject(String featureName)
	{
		if(
				(featureName.equals("DirectionPrimary"	))||
				
				(featureName.equals("mtgClass"			))
				)
		{
			return true;
		}
		
		return false;
	}
	
	private void reinstantiateMTGInternal(Node node)
	{
		//Check if type for this node is obsolete
		boolean isObsolete = isObsoleteInstance(node);
		Node newNode = null;
		
		//if this node needs to be replaced
		if(isObsolete)
		{
			//buffer for all incoming/outgoing nodes and edges from this node
			ArrayList<Node> inNodes = new ArrayList<Node>();
			ArrayList<Integer> inEdges = new ArrayList<Integer>();
			
			ArrayList<Node> outNodes = new ArrayList<Node>();
			ArrayList<Integer> outEdges = new ArrayList<Integer>();
			
			//fill buffer with node references and edge types
			for(Edge e = node.getFirstEdge(); e!=null; e = e.getNext(node))
			{
				//incoming edge
				if(e.getTarget()==node)
				{
					inEdges.add(new Integer(e.getEdgeBits()));
					inNodes.add(e.getSource());
				}
				
				//outgoing edge
				if(e.getSource()==node)
				{
					outEdges.add(new Integer(e.getEdgeBits()));
					outNodes.add(e.getTarget());
				}
			}
			
			//create new node using new type
			Type newType = getNewType(node);
			try {
				newNode = (Node)(newType.newInstance());
			} catch (Throwable e) {
				e.printStackTrace();
			} 
			
			if(newNode!=null)
			{
				//copy node
				copyNodeFields(node, newNode);
				
				//connect new node to incoming buffered connections
				for(int i=0; i<inNodes.size(); ++i)
				{
					inNodes.get(i).addEdgeBitsTo(newNode, inEdges.get(i).intValue(), null);
				}
				//connect new node to outgoing buffered connections
				for(int j=0; j<outNodes.size(); ++j)
				{
					newNode.addEdgeBitsTo(outNodes.get(j), outEdges.get(j).intValue(), null);
				}
				
				//disconnect old node
				for(Edge e = node.getFirstEdge(); e!=null; e = e.getNext(node))
				{
					if(e.getSource()==node)
					{
						node.removeEdgeBitsTo(e.getTarget(), e.getEdgeBits(), null);
					}
					else if(e.getTarget()==node)
					{
						e.getSource().removeEdgeBitsTo(node, e.getEdgeBits(), null);
					}
				}
			}
		}
		else
		{
			newNode = node;
		}
		
		//Buffer for outgoing target nodes from current node
		ArrayList<Node> outNodes = new ArrayList<Node>();
		//fill buffer with node references and edge types
		for(Edge e = newNode.getFirstEdge(); e!=null; e = e.getNext(newNode))
		{
			if(e.getSource() == newNode)
			{
				outNodes.add(e.getTarget());
			}
		}
		for(int i=0; i<outNodes.size(); ++i)
		{
			Node outNode = outNodes.get(i);
			if(isMTGNode(outNode))
			{
				//do not re-visit updated MTG nodes - multiple paths to an mtg node may exist
				if(isObsoleteInstance(outNode))
				{
					reinstantiateMTGInternal(outNode);
				}
			}
			else
			{
				reinstantiateMTGInternal(outNode);
			}
		}
	}

	@Override
	protected void activateImpl ()
	{
		if (disabled)
		{
			activationStamp = -1;
			return;
		}
		Object f = getRegistry ().getProjectFile (getName ());
		if (f != null)
		{
			getRegistry ().addFileSystemListener (this);
			getRegistry ().getFileSystem ().setMimeType (f, mimeType);
		}
		FileSource fs = toFileSource ();
		FilterSource s = IO.createPipeline (fs, IOFlavor.RESOURCE_LOADER);
		if (!(s instanceof ObjectSource))
		{
			return;
		}
		s.initProgressMonitor (UI.createProgressAdapter (Workbench.get (this)));
		try
		{
			final ResourceLoader rl = (ResourceLoader) ((ObjectSource) s)
				.getObject ();
			deactivationCategory = rl.getJoinedDeactivationCategory ();
			if (activationStamp != getRegistry ().getActivationStamp ())
			{
				activationStamp = getRegistry ().getActivationStamp ();
				((Item) getAxisParent ()).forAll (null, null, new ItemVisitor ()
				{
					public void visit (Item item, Object info)
					{
						if (!item.isActivated () && (item instanceof SourceFile)
							 && !((SourceFile) item).disabled
							 && (((SourceFile) item).activationStamp != activationStamp)
							 && rl.addResource (((SourceFile) item).toFileSource ()))
						{
							((SourceFile) item).activationStamp = activationStamp;
						}
					}
				}, null, false);
				rl.loadResource (getRegistry ());
				
				//yong 25 apr 2012
				//after recompilation, ensure that nodes in the graph are re-instantiated to use the new types
				if(this.getName().contains("generated.xl"))
					reinstantiateMTG();
			}
		}
		catch (Exception e)
		{
			Workbench.get (this).logGUIInfo (
				IO.I18N.msg ("openfile.failed", fs.getSystemId ()), e);
		}
		finally
		{
			s.setProgress (null, ProgressMonitor.DONE_PROGRESS);
		}
	}

	@Override
	protected void deactivateImpl ()
	{
		getRegistry ().removeFileSystemListener (this);
	}

	@Override
	protected Object getDescriptionImpl (String type)
	{
		if (NAME.equals (type))
		{
			return IO.toPath (getName ());
		}
		Object d = super.getDescriptionImpl (type);
		return ICON.equals (type) ? UI.getIcon (getName (), mimeType
			.getMediaType (), (IconSource) d, this, true) : d;
	}

	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}

	public void show (Context ctx)
	{
		show (ctx, null);
	}

	public void show (Context ctx, String ref)
	{
		MimeTypeItem m = getMimeTypeItem ();
		if (m != null)
		{
			if (editable && m.isEditable ())
			{
				Panel[] p = ctx.getWindow ().getPanels (this);
				TextEditor t;
				for (int i = 0; i < p.length; i++)
				{
					t = (TextEditor) p[i];
					if (Utils.isContained (getName (), t.getDocuments ()))
					{
						t.openDocument (getName (), ref);
						t.show (false, null);
						return;
					}
				}
				if (p.length > 0)
				{
					t = (TextEditor) p[0];
				}
				else
				{
					t = (TextEditor) PanelFactory.createPanel (ctx,
						"/ui/panels/texteditor", null);
				}
				t.openDocument (getName (), ref);
				t.show (false, null);
			}
			else if (m.isViewable ())
			{
				ctx.getWorkbench ().showViewerPanel (getName (), getName (),
					this);
			}
		}
	}

	public void showLater (Context ctx)
	{
		UI.getJobManager (ctx).runLater (new Command ()
		{
			public void run (Object arg, Context ctx)
			{
				show (ctx, null);
			}

			public String getCommandName ()
			{
				return null;
			}
		}, null, ctx, JobManager.ACTION_FLAGS);
	}

	public boolean isAvailable (Context ctx)
	{
		return true;
	}

	public boolean isEnabled (Context ctx)
	{
		return true;
	}

	public int getUINodeType ()
	{
		return de.grogra.pf.ui.tree.UINodeHandler.NT_SPECIAL;
	}

	public boolean evaluateBoolean (Object o)
	{
		return o instanceof TextEditor;
	}

	@Override
	public void addRequiredFiles (java.util.Collection list)
	{
		Object f = getRegistry ().getProjectFile (getName ());
		if (f != null)
		{
			list.add (f);
		}
	}

	public static SourceFile get (RegistryContext ctx, String systemId)
	{
		Item dir = ctx.getRegistry ().getItem ("/project/objects/files");
		return (dir != null) ? (SourceFile) dir.getItem (systemId) : null;
	}

	public void treeNodesInserted (TreeModelEvent e)
	{
	}

	public void treeNodesRemoved (TreeModelEvent e)
	{
	}

	public void treeNodesChanged (TreeModelEvent e)
	{
		if (FileSystem.isContainedInChildren (getRegistry ().getProjectFile (
			getName ()), e))
		{
			refresh ((Item) getAxisParent (), this, deactivationCategory, new Command ()
				 {
					public String getCommandName ()
					{
						return null;
					}

					public void run (Object info, Context context)
					{
						if (Utils.getBoolean (UI.getOptions (context), "saveProjectOnFileModification"))
						{
							context.getWorkbench ().save (false);
						}
					}
				 });
		}
	}

	public void treeStructureChanged (TreeModelEvent e)
	{
	}


	public static void refresh (final Item dir, final SourceFile file, final String category, final Command afterRefresh)
	{
		class Deactivator extends LockProtectedCommand implements
				ItemVisitor
		{
			Deactivator (Workbench w)
			{
				super (w.getRegistry ().getProjectGraph (), true,
					JobManager.ACTION_FLAGS);
			}

			@Override
			public String getCommandName ()
			{
				return null;
			}

			@Override
			protected void runImpl (Object info, Context context, Lock lock)
			{
				dir.forAll (null, null, this, null, false);
				dir.getRegistry ().activateItems ();
				if (afterRefresh != null)
				{
					UI.getJobManager (context).runLater (afterRefresh, info, context, JobManager.UPDATE_FLAGS);
				}
			}

			public void visit (Item item, Object info)
			{
				if (!(item.isActivated () && (item instanceof SourceFile)))
				{
					return;
				}
				if ((item == file)
					|| (((SourceFile) item).activationStamp == -1)
					|| ((category != null)
						&& (item instanceof SourceFile) && category
						.equals (((SourceFile) item).deactivationCategory)))
				{
					item.deactivate ();
				}
			}
		}

		Workbench w = Workbench.get (dir);
		w.getJobManager ().runLater (new Deactivator (w), null, w,
			JobManager.ACTION_FLAGS);
	}


	//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field mimeType$FIELD;
	public static final NType.Field editable$FIELD;
	public static final NType.Field disabled$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SourceFile.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((SourceFile) o).editable = (boolean) value;
					return;
				case 2:
					((SourceFile) o).disabled = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 1:
					return ((SourceFile) o).isEditable ();
				case 2:
					return ((SourceFile) o).isDisabled ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((SourceFile) o).mimeType = (MimeType) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((SourceFile) o).mimeType;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new SourceFile ());
		$TYPE.addManagedField (mimeType$FIELD = new _Field ("mimeType", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (MimeType.class), null, 0));
		$TYPE.addManagedField (editable$FIELD = new _Field ("editable", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
		$TYPE.addManagedField (disabled$FIELD = new _Field ("disabled", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 2));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new SourceFile ();
	}

	public boolean isEditable ()
	{
		return editable;
	}

	public boolean isDisabled ()
	{
		return disabled;
	}

	public void setDisabled (boolean value)
	{
		this.disabled = (boolean) value;
	}

//enh:end

}

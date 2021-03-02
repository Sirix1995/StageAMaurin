package de.grogra.animation.handler.animationnodes;

import java.util.Set;
import de.grogra.persistence.PersistenceField;
import de.grogra.animation.interpolation.Interpolation;
import de.grogra.animation.interpolation.linear.LinearInterpolation;
import de.grogra.graph.impl.Node;

public class AnimationNode extends Node {

	//TODO: use perl script to create a real groimp node type
	//TODO: es kann property nicht abgespeichert werden,
	//da PersistenceField/IndirectField nicht serialisierbar ist
	
	final public static int ANIM_EDGE = Node.MIN_UNUSED_SPECIAL_OF_TARGET;
	
	private PersistenceField property;
	
	private Interpolation interpolation;
		
	public AnimationNode() {
	}
	
	public void setProperty(PersistenceField property) {
		this.property = property;
		this.interpolation = new LinearInterpolation();
		this.interpolation.setInterpolationType(property.getType());
	}
	
	public PersistenceField getProperty() {
		return property;
	}
	
	public boolean putValue(int time, Object value) {
		return interpolation.putValue(time, value);
	}
	
	public void changeValue(int oldTime, int newTime, Object value) {
		interpolation.changeValue(oldTime, newTime, value);
	}
	
	public void getTimes(Set<Integer> times) {
		interpolation.getTimes(times);
	}
	
	public Object getValue(double time) {
		return interpolation.getValue(time);
	}
	
	private static void initType() {
		$TYPE.declareSpecialEdge(ANIM_EDGE, "AnimationEdge", new Node[0]);
	}
	
	//enh:insert initType();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new AnimationNode ());
		initType();
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
		return new AnimationNode ();
	}

//enh:end
	
}

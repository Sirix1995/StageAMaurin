package de.grogra.animation;

import de.grogra.graph.impl.GraphTransaction.Consumer;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.persistence.Transaction.Reader;

public abstract class AnimConsumer implements Transaction.ExtendedConsumer, Consumer {
	
	public void setField(PersistenceCapable o, PersistenceField field,
			int[] indices, Reader reader) {
		// not called because this is an extended consumer
	}
}

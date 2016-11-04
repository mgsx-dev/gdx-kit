package net.mgsx.game.core.helpers;

import java.util.Iterator;

public abstract class AdaptIterable<T,V> implements Iterable<V>{

	private Iterable<T> original;
	
	public AdaptIterable(Iterable<T> original) {
		super();
		this.original = original;
	}

	@Override
	public Iterator<V> iterator() 
	{
		return new Iterator<V>() {
			private Iterator<T> i = original.iterator();
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}

			@Override
			public V next() {
				return adapt(i.next());
			}
			@Override
			public void remove() {
				i.remove();
			}
		};
	}
	
	protected abstract V adapt(T element);

}

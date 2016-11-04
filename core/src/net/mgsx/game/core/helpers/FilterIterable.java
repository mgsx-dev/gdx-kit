package net.mgsx.game.core.helpers;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class FilterIterable<T> implements Iterable<T>{

	private Iterable<T> original;
	
	public FilterIterable(Iterable<T> original) {
		super();
		this.original = original;
	}

	@Override
	public Iterator<T> iterator() 
	{
		return new Iterator<T>() {
			private Iterator<T> i = original.iterator();
			private T current = null;
			private void update(){
				if(current == null){
					while(i.hasNext()){
						current = i.next();
						if(keep(current)){
							break;
						}
						current = null;
					}
				}
			}
			@Override
			public boolean hasNext() {
				update();
				return current != null;
			}

			@Override
			public T next() {
				update();
				if(current == null) throw new NoSuchElementException();
				T element = current;
				current = null;
				return element;
			}
			@Override
			public void remove() {
				i.remove();
				current = null;
			}
		};
	}
	
	protected abstract boolean keep(T element);

}

package net.mgsx.game.examples.openworld.utils;

import com.badlogic.gdx.utils.ObjectSet;

/**
 * Maintain a set object objects and compute delta.
 * 
 * <p>
 * Usage :
 * <ul>
 * <li>Just call {@link #replace(Iterable)}</li>
 * <li>or first call {@link #reset()} delta, then many calls {@link #add(Object)} and {@link #addAll(Iterable)}</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Finally, you can read :
 * <ul>
 * <li>{@link #addedItems()} : which items are present and was not present before.</li>
 * <li>{@link #removedItems()} : which items wasn't present before and are present now.</li>
 * <li>{@link #allItems()} : all current items present now</li>
 * </ul>
 * </p>
 * 
 * <p>{@link #clear()} will reset the delta set to its initial state (all sets are cleared)</p>
 * 
 * @todo move to gdx utils
 * 
 * @author mgsx
 *
 * @param <T>
 */
public class DeltaSet<T> 
{
	private ObjectSet<T> oldItems = new ObjectSet<T>();
	private ObjectSet<T> newItems = new ObjectSet<T>();
	private ObjectSet<T> addedItems = new ObjectSet<T>();
	
	public void reset(){
		// swap sets : previous state become old state
		ObjectSet<T> tmp = oldItems;
		oldItems = newItems;
		newItems = tmp;
		// compute delta
		newItems.clear();
		addedItems.clear();
	}

	public void replace(Iterable<T> items){
		reset();
		addAll(items);
	}
	
	public void add(T item) {
		if(newItems.add(item)){
			if(!oldItems.remove(item)){
				addedItems.add(item);
			}
		}
	}
	
	public void addAll(Iterable<T> items) {
		for(T item : items){
			add(item);
		}
	}
	
	public Iterable<T> allItems(){
		return newItems;
	}

	public Iterable<T> removedItems(){
		return oldItems;
	}
	
	public Iterable<T> addedItems(){
		return addedItems;
	}
	
	public void clear(){
		oldItems.clear();
		addedItems.clear();
		newItems.clear();
	}
}

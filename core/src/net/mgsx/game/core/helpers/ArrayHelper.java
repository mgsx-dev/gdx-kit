package net.mgsx.game.core.helpers;

import com.badlogic.gdx.utils.Array;

public class ArrayHelper {

	public static <T> void addAll(Array<T> array, Iterable<T> elements)
	{
		for(T element : elements) array.add(element);
	}
	public static <T> Array<T> array(Iterable<T> elements)
	{
		Array<T> array = new Array<T>();
		addAll(array, elements);
		return array;
	}
}

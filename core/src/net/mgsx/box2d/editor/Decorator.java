package net.mgsx.box2d.editor;

import com.badlogic.gdx.utils.Array;

class Decorator<T>
{
	public static interface Mapper<T>{
		public String map(T object);
	}
	public final T object;
	public final String label;
	public Decorator(T object, String label) {
		super();
		this.object = object;
		this.label = label;
	}
	@Override
	public String toString() {
		return label;
	}
	public static <T> Array<Decorator<T>> decorate(Array<T> objects, Decorator.Mapper<T> mapper, boolean defaultEmpty){
		Array<Decorator<T>> r = new Array<Decorator<T>>();
		if(defaultEmpty) r.add(new Decorator<T>(null, ""));
		for(T object : objects) r.add(new Decorator<T>(object, mapper.map(object)));
		return r;
	}
}
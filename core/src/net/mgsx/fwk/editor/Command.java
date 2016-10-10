package net.mgsx.fwk.editor;

import java.lang.reflect.Field;

import net.mgsx.box2d.editor.BodyItem;

import com.badlogic.gdx.math.Vector2;

public class Command {
	public void commit(){}
	public void rollback(){}
	
	public static class SetCommand<T> extends Command
	{
		private T backup, value;
		private Object object;
		private Field field;
		
		public SetCommand(Object object, Field field, T value) {
			super();
			this.object = object;
			this.field = field;
			this.value = value;
		}
		@SuppressWarnings("unchecked")
		@Override
		public void commit() {
			backup = (T)ReflectionHelper.get(object, field);
			ReflectionHelper.set(object, field, value);
		}
		@Override
		public void rollback() {
			ReflectionHelper.set(object, field, backup);
		}
	}
	
	
}

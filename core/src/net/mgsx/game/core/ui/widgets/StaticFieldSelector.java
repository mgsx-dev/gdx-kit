package net.mgsx.game.core.ui.widgets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class StaticFieldSelector<T> implements FieldEditor 
{
	private Class container;
	private Class<T> type;
	private ObjectMap<String, T> values;
	
	

	public StaticFieldSelector(Class container, Class<T> type) {
		super();
		this.container = container;
		this.type = type;
	}

	private void scan(){
		if(values == null){
			values = new ObjectMap<String, T>();
			values.put("", null);
			for(Field field : container.getFields()){
				if(Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && type.isAssignableFrom(field.getType())){
					T value = (T)ReflectionHelper.get(null, field);
					values.put(field.getName(), value);
				}
			}
		}
	}
	
	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		scan();
		
		final SelectBox<String> box = new SelectBox<String>(skin);
		Array<String> items = values.keys().toArray();
		items.sort();
		box.setItems(items);
		String current = values.findKey(accessor.get(), true);
		if(current != null){
			box.setSelected(current);
		}
		box.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				accessor.set(values.get(box.getSelected()));
			}
		});
		return box;
	}

}

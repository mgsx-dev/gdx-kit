package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class EnumWidget<T extends Enum> implements FieldEditor 
{
	final private ObjectMap<String, T> values = new ObjectMap<String, T>();

	public EnumWidget(Class<T> enumeration) {
		super();
		for(T value : enumeration.getEnumConstants()){
			values.put(value.name(), value);
		}
	}

	// TODO refactor as SelectorWidget ...
	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
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

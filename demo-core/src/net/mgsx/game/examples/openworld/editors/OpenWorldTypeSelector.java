package net.mgsx.game.examples.openworld.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;

public class OpenWorldTypeSelector implements FieldEditor {

	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		// Assume accessor is a string
		if(accessor.getType() != String.class){
			throw new GdxRuntimeException("editor expect  String type get " + accessor.getType().getName());
		}
		
		final SelectBox<String> selector = new SelectBox<String>(skin);
		selector.setItems(OpenWorldModel.getAllTypes());
		String value = accessor.get(String.class);
		selector.setSelected(value);
		selector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				accessor.set(selector.getSelected());
			}
		});
		
		return selector;
	}

}

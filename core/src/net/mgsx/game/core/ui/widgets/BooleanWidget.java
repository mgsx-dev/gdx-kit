package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class BooleanWidget implements FieldEditor
{
	public static final BooleanWidget instance = new BooleanWidget();
	
	public Actor create(final Accessor accessor, Skin skin) {
		
		final TextButton button = new TextButton(String.valueOf(accessor.get()), skin, "toggle");
		button.setChecked((Boolean)accessor.get());
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if((Boolean)accessor.get() != button.isChecked()){
					accessor.set(button.isChecked());
					button.setText(String.valueOf(accessor.get()));
				}
			}
		});
		
		return button;
	}
}

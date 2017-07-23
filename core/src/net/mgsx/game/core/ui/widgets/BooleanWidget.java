package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

abstract public class BooleanWidget implements FieldEditor
{
	public static final BooleanWidget instance = labeled("true", "false");
	
	public static BooleanWidget labeled(final String labelOnOff){
		return new BooleanWidget(){
			@Override
			public Actor create(final Accessor accessor, Skin skin) {
				final TextButton button = new TextButton(labelOnOff, skin, "toggle");
				button.setChecked((Boolean)accessor.get());
				button.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						if((Boolean)accessor.get() != button.isChecked()){
							accessor.set(button.isChecked());
						}
					}
				});
				return button;
			}
		};
	}
	public static BooleanWidget labeled(final String labelOn, final String labelOff){
		return new BooleanWidget(){
			@Override
			public Actor create(final Accessor accessor, Skin skin) {
				final TextButton button = new TextButton((Boolean)accessor.get() ? labelOn : labelOff, skin, "toggle");
				button.setChecked((Boolean)accessor.get());
				button.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						if((Boolean)accessor.get() != button.isChecked()){
							accessor.set(button.isChecked());
							button.setText((Boolean)accessor.get() ? labelOn : labelOff);
						}
					}
				});
				return button;
			}
		};
	}
}

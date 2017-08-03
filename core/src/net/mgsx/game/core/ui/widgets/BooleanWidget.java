package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

abstract public class BooleanWidget implements FieldEditor
{
	public static final BooleanWidget instance = labeled("true", "false");
	
	public static BooleanWidget labeled(final String labelOnOff){
		return labeled(labelOnOff, labelOnOff);
	}
	public static BooleanWidget labeled(final String labelOn, final String labelOff){
		return new BooleanWidget(){
			@Override
			public Actor create(final Accessor accessor, Skin skin) {
				final boolean realtime = accessor.config() != null && accessor.config().realtime();
				final boolean readonly = accessor.config() != null && accessor.config().readonly();
				if(readonly){
					final Label label = new Label((Boolean)accessor.get() ? labelOn : labelOff, skin){
						@Override
						public void act(float delta) {
							if(realtime){
								boolean value = accessor.get(Boolean.class);
								setText(value ? labelOn : labelOff);
							}
							super.act(delta);
						}
					};
					label.setColor(Color.CYAN);
					return label;
				}else{
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
			}
		};
	}
}

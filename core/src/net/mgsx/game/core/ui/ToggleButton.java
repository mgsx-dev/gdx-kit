package net.mgsx.game.core.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Toggle button with text
 */
public class ToggleButton extends TextButton
{

	public ToggleButton(final String textOn, final String textOff, final boolean checked, Skin skin) {
		super(checked ? textOn : textOff, skin, "toggle");
		setChecked(checked);
		addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setText(isChecked() ? textOn : textOff);
			}
		});
	}

}

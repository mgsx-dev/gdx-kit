package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

abstract public class VoidWidget implements FieldEditor
{
	public static final VoidWidget instance = new VoidWidget(){
		@Override
		public Actor create(final Accessor accessor, Skin skin) {
			TextButton button = new TextButton(accessor.getName(), skin);
			button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					accessor.get();
				}
			});
			return button;
		}
	};
}

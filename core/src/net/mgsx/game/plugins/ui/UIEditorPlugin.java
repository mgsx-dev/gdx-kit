package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;

public class UIEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		// FIXME not in editor ...
		
		editor.entityEngine.addSystem(new ScalarSystem());
		
		editor.entityEngine.addSystem(new WidgetSystem(editor));
		
		editor.addTool(new WidgetTool("VSlider", editor, new WidgetFactory() {
			@Override
			public Actor createActor(Engine engine, Entity entity, Skin skin) {
				return new Slider(0, 1, .001f, true, skin);
			}
		}){});
		editor.addTool(new WidgetTool("HSlider", editor, new WidgetFactory() {
			@Override
			public Actor createActor(Engine engine, Entity entity, Skin skin) {
				return new Slider(0, 1, .001f, false, skin);
			}
		}){});
		
		// TODO more examples ...
		
	}

}

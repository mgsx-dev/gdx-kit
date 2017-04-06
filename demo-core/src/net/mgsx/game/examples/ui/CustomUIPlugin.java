package net.mgsx.game.examples.ui;

import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.ui.ScalarSystem;
import net.mgsx.game.plugins.ui.WidgetTool;

// TODO put in tutorials !
public class CustomUIPlugin extends EditorPlugin
{
	@EditableSystem
	public static class Example extends EntitySystem {
		@Inject public ScalarSystem scalar;
		@Editable(readonly=true, realtime=true) public float value;

		@Override
		public void update(float deltaTime) {
			value = scalar.get("example-var");
		}
	}

	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new WidgetTool("Custom UI", editor, new MySliderFactory()){});
		editor.entityEngine.addSystem(new Example());
	}

}

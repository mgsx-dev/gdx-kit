package net.mgsx.game.plugins.g3d.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.g3d.systems.G3DBoundaryDebugSystem;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

public class G3DEditor implements GlobalEditorPlugin
{

	@Override
	public Actor createEditor(final EditorScreen editor, Skin skin) 
	{
		Table table = new Table(skin);
		
		EntityEditor e = new EntityEditor(skin, true);
		e.generate(editor.entityEngine.getSystem(G3DCullingSystem.class), table);
		e.generate(editor.entityEngine.getSystem(G3DRendererSystem.class), table);
		e.generate(editor.entityEngine.getSystem(G3DBoundaryDebugSystem.class), table);
		
		return table;
	}

}

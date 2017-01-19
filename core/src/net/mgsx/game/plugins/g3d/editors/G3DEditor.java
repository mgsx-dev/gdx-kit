package net.mgsx.game.plugins.g3d.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;
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
		
		table.add("Point lights");
		Label label = new Label("", skin){
			private ImmutableArray<Entity> visibles = editor.entityEngine.getEntitiesFor(Family.all(PointLightComponent.class).exclude(Hidden.class).get());
			private ImmutableArray<Entity> all = editor.entityEngine.getEntitiesFor(Family.all(PointLightComponent.class).get());
			
			@Override
			public void act(float delta) {
				String newText = String.format("%d/%d", visibles.size(), all.size());
				setText(newText);
				super.act(delta);
			}
		};
		table.add(label);
		
		return table;
	}

}

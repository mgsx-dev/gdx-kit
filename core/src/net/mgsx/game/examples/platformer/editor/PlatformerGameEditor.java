package net.mgsx.game.examples.platformer.editor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.examples.platformer.core.BonusComponent;
import net.mgsx.game.examples.platformer.core.PlayerComponent;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.G3DModel;

public class PlatformerGameEditor extends EditorPlugin {

	@Override
	public void initialize(final Editor editor) 
	{
//		editor.addTool(new ClickTool("Player", editor) {
//			@Override
//			protected void create(Vector2 position) 
//			{
//				Entity player = editor.createEntity();
//				editor.entityEngine.addEntity(player);
//				
//				G3DModel model = new G3DModel();
//				model.modelInstance = new ModelInstance(editor.assets.get("player.g3dj", Model.class));
//				model.animationController = new AnimationController(model.modelInstance);
//				player.add(model);
//				
//				// TODO add Box2D shape ?
//				
//				PlayerComponent logic = new PlayerComponent();
//				logic.initialize(player);
//				player.add(logic);
//			}
//		});
		editor.addTool(new ComponentTool("Player Logic", editor, Family.all(G3DModel.class, Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				PlayerComponent logic = new PlayerComponent();
				logic.initialize(editor.entityEngine, entity);
				entity.add(logic);
				
				return logic;
			}
		});
		
		editor.addTool(new ComponentTool("Bonus Logic", editor, Family.all(G3DModel.class, Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				BonusComponent logic = new BonusComponent();
				logic.initialize(editor.entityEngine, entity);
				entity.add(logic);
				
				return logic;
			}
		});

	}
}

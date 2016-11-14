package net.mgsx.game.examples.platformer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.components.LogicComponent;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.platformer.components.EnemyComponent;
import net.mgsx.game.examples.platformer.components.KeyboardController;
import net.mgsx.game.examples.platformer.components.PlayerComponent;
import net.mgsx.game.examples.platformer.components.PlayerController;
import net.mgsx.game.examples.platformer.components.TreeComponent;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@PluginDef(dependencies={PlatformerPlugin.class})
public class PlatformerEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
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
		
		editor.addTool(new Tool("Keyboard Player", editor) {
			
			@Override
			protected void activate() 
			{
				Entity entity = editor.currentEntity();
				
				entity.add(editor.entityEngine.createComponent(PlayerController.class));
				KeyboardController keys = editor.entityEngine.createComponent(KeyboardController.class);
				keys.up = Input.Keys.UP;
				keys.down = Input.Keys.DOWN;
				keys.left = Input.Keys.LEFT;
				keys.right = Input.Keys.RIGHT;

				keys.jump = Input.Keys.Z;
				keys.grab = Input.Keys.A;
				entity.add(keys);
				
				end();
			}
		});
		
		editor.addTool(new ComponentTool("Enemy Logic", editor, Family.all(G3DModel.class, Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				LogicComponent logic = new EnemyComponent();
				logic.initialize(editor.entityEngine, entity);
				return logic;
			}
		});

		editor.addTool(new ComponentTool("Tree Logic", editor, Family.all(G3DModel.class, Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				LogicComponent logic = new TreeComponent();
				logic.initialize(editor.entityEngine, entity);
				return logic;
			}
		});
//		editor.addTool(new ComponentTool("Bee Logic", editor, Family.all(G3DModel.class).get()) {
//			
//			@Override
//			protected Component createComponent(Entity entity) 
//			{
////				StateMachineComponent smc = editor.entityEngine.createComponent(StateMachineComponent.class);
////				smc.initialState = BeeState.INIT;
//				return editor.entityEngine.createComponent(FlyingState.class);
//			}
//		});
	}

}

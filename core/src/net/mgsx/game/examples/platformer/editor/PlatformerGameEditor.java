package net.mgsx.game.examples.platformer.editor;

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
import net.mgsx.game.examples.platformer.PlatformerPlugin;
import net.mgsx.game.examples.platformer.components.BonusComponent;
import net.mgsx.game.examples.platformer.components.CavernComponent;
import net.mgsx.game.examples.platformer.components.ClimbZone;
import net.mgsx.game.examples.platformer.components.EnemyComponent;
import net.mgsx.game.examples.platformer.components.EnvComponent;
import net.mgsx.game.examples.platformer.components.LianaZone;
import net.mgsx.game.examples.platformer.components.PlatformComponent;
import net.mgsx.game.examples.platformer.components.PlayerComponent;
import net.mgsx.game.examples.platformer.components.PulleyComponent;
import net.mgsx.game.examples.platformer.components.SpiderComponent;
import net.mgsx.game.examples.platformer.components.TreeComponent;
import net.mgsx.game.examples.platformer.components.WaterZone;
import net.mgsx.game.examples.platformer.systems.input.KeyboardController;
import net.mgsx.game.examples.platformer.systems.input.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@PluginDef(dependencies={PlatformerPlugin.class})
public class PlatformerGameEditor extends EditorPlugin {

	@Override
	public void initialize(final EditorScreen editor) 
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

		
		editor.addTool(new ComponentTool("Bonus Logic", editor, Family.all(G3DModel.class, Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new BonusComponent();
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

		editor.addTool(new ComponentTool("Climb Zone", editor, Family.all(Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new ClimbZone();
			}
		});
		
		editor.addTool(new ComponentTool("Water Zone", editor, Family.one(Box2DBodyModel.class, G3DModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new WaterZone();
			}
		});
		
		editor.addTool(new ComponentTool("Liana Zone", editor, Family.one(Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new LianaZone();
			}
		});
		
		editor.addTool(new ComponentTool("Platform", editor, Family.all(Box2DBodyModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new PlatformComponent();
			}
		});
		
		editor.addTool(new ComponentTool("Pulley", editor, Family.all(Box2DJointModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new PulleyComponent();
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
		editor.addTool(new ComponentTool("Cavern Logic", editor, Family.all(G3DModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new CavernComponent();
			}
		});
		editor.addTool(new ComponentTool("Env Logic", editor, Family.all(G3DModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new EnvComponent();
			}
		});
		editor.addTool(new ComponentTool("Spider Logic", editor, Family.all(G3DModel.class).get()) {
			
			@Override
			protected Component createComponent(Entity entity) 
			{
				return new SpiderComponent();
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

		
		
		editor.addGlobalEditor("Water Effect", new WaterEffectEditor());

	}
}

package net.mgsx.game.examples.platformer.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Interpolation;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.examples.platformer.PlatformerGame;
import net.mgsx.game.examples.platformer.PlatformerWorkflow;
import net.mgsx.game.examples.platformer.systems.PlatformerHUDSystem;
import net.mgsx.game.examples.platformer.systems.input.KeyboardControllerSystem;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.RenderingComponent;
import net.mgsx.game.plugins.core.components.Transform3DComponent;
import net.mgsx.game.plugins.core.components.Translation3D;

public class LevelScreen extends GameScreen
{
	private enum LevelStates implements State<LevelScreen>
	{
		INIT(){
			@Override
			public void enter(LevelScreen screen) 
			{
				screen.entityEngine.getSystem(KeyboardControllerSystem.class).setProcessing(false);
				
				Entity camera = EntityHelper.first(screen.entityEngine, Family.all(CameraComponent.class, RenderingComponent.class).get());
				if(camera != null){
					CameraComponent cam = CameraComponent.components.get(camera);
					
					Transform3DComponent t3d = screen.entityEngine.createComponent(Transform3DComponent.class);
					camera.add(t3d);
					Translation3D anim = screen.entityEngine.createComponent(Translation3D.class);
					camera.add(anim);
					
					// TODO better to create animation in blender ...
					
					anim.interpolation = Interpolation.sine;
					anim.duration = 5;
					anim.target.set(cam.camera.position);
					anim.origin.set(anim.target).add(0,0,4);
				}
			}
			@Override
			public void update(LevelScreen screen) 
			{
				Entity camera = EntityHelper.first(screen.entityEngine, Family.all(CameraComponent.class, RenderingComponent.class, Translation3D.class).get());
				if(camera != null){
					Translation3D animation = Translation3D.components.get(camera);
					if(animation.time > animation.duration){
						camera.remove(Translation3D.class);
						camera.remove(Transform3DComponent.class);
						screen.fsm.changeState(TITLE);
					}
				}else{
					screen.fsm.changeState(TITLE);
				}
			}
		},
		TITLE(){
			@Override
			public void enter(final LevelScreen screen) {
				// TODO add actor on stage
				screen.entityEngine.getSystem(PlatformerHUDSystem.class).displayTitle("Level 1", new Runnable() {
					@Override
					public void run() {
						screen.fsm.changeState(PLAYER_START);
					}
				});;
			}
		},
		PLAYER_START(){
			@Override
			public void enter(LevelScreen screen) {
				screen.entityEngine.getSystem(KeyboardControllerSystem.class).setProcessing(true);
				screen.entityEngine.getSystem(PlatformerHUDSystem.class).show();
			}
			@Override
			public void update(LevelScreen screen) {
				// TODO if player is dead, then finish game (back to title).
				if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
				{
					screen.fsm.changeState(END);
				}
			}
		},
		END(){
			@Override
			public void enter(LevelScreen screen) {
				screen.game.abortGame();
			}
		}
		
		;

		@Override
		public void enter(LevelScreen screen) {
		}
		@Override
		public void update(LevelScreen screen) {
		}
		@Override
		public void exit(LevelScreen screen) {
		}
		@Override
		public boolean onMessage(LevelScreen screen, Telegram telegram) {
			return false;
		}
	}
	
	private StateMachine<LevelScreen, State<LevelScreen>> fsm;
	
	private final PlatformerWorkflow game;
	
	public LevelScreen(PlatformerGame game, Engine engine) 
	{
		super(game.getAssets(), engine);
		this.game = game;
		this.fsm = new DefaultStateMachine<LevelScreen, State<LevelScreen>>(this);
	}
	
	@Override
	public void show() 
	{
		super.show();
		
		// level boot sequence : 
		// * start camera animation
		// * then display level name
		// * then enable player commands
		
		fsm.changeState(LevelStates.INIT);
	}
	
	@Override
	public void render(float delta) 
	{
		fsm.update();
		super.render(delta);
	}

}

package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;

public class SecretRenderSystem extends EntitySystem
{
	private ImmutableArray<Entity> secrets, discovery;
	private ShapeRenderer shape;
	private G3DRendererSystem modelRenderer;
	private GameScreen engine;
	
	public SecretRenderSystem(GameScreen engine) {
		super(GamePipeline.RENDER + 1); // TODO use custom pipeline (inherit from base pipeline ?)
		this.engine = engine;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		modelRenderer = engine.getSystem(G3DRendererSystem.class);
		secrets = engine.getEntitiesFor(Family.all(G3DModel.class, SecretComponent.class).get());
		discovery = engine.getEntitiesFor(Family.all(Transform2DComponent.class, SecretDiscoveryComponent.class).get());
		shape = new ShapeRenderer();
		
		// auto add/remove hidden component. TODO use a flag in model ? (customRender) to exclude it from normal rendering.
		engine.addEntityListener(Family.all(SecretComponent.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				entity.remove(Hidden.class);
			}
			@Override
			public void entityAdded(Entity entity) {
				entity.add(getEngine().createComponent(Hidden.class));
			}
		});
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// TODO other system with inverted glClearDepthf / glDepthRangef value to see only in range !
		
		// write discovery in depth buffer only
		Gdx.gl.glClearDepthf(1);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearDepthf(1); // restore to default
		shape.setProjectionMatrix(engine.camera.combined);
		shape.begin(ShapeType.Filled);
		Gdx.gl.glColorMask(false, false, false, false); // XXX debug here
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); // required to write in depth buffer
		Gdx.gl.glDepthFunc(GL20.GL_ALWAYS);
		Gdx.gl.glDepthRangef(0,0);
		for(Entity entity : discovery){
			SecretDiscoveryComponent discovery = SecretDiscoveryComponent.components.get(entity);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			shape.circle(transform.position.x + discovery.offset.x, transform.position.y + discovery.offset.y, discovery.radius, 36);
		}
		shape.end();
		Gdx.gl.glColorMask(true, true, true, true);
		Gdx.gl.glDepthRangef(0,1); // restore back
		
		// write secrets with depth test
		modelRenderer.modelBatch.begin(engine.camera);
		for(Entity entity : secrets){
			G3DModel model = G3DModel.components.get(entity);
			modelRenderer.modelBatch.render(model.modelInstance, modelRenderer.environment);
		}
		modelRenderer.modelBatch.end();
	}
}

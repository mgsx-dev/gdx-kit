package net.mgsx.game.examples.procedural.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.procedural.components.LSystem3D;
import net.mgsx.game.examples.procedural.model.Generator;
import net.mgsx.game.examples.procedural.model.l2d.Context2D;

@EditableSystem
public class LSystem3DRender extends IteratingSystem
{
	@Editable
	public int depth = 3;
	
	private ShapeRenderer renderer;
	private GameScreen game;
	public LSystem3DRender(GameScreen game) {
		super(Family.all(LSystem3D.class).get(), GamePipeline.RENDER);
		this.game = game;
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.begin(ShapeType.Line);
		renderer.setProjectionMatrix(game.camera.combined);
		super.update(deltaTime);
		renderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		LSystem3D lsystem = LSystem3D.components.get(entity);
		if(lsystem.system != null){
			if(lsystem.generator == null){
				lsystem.generator = new Generator<Context2D>(Context2D.class, lsystem.system);
				lsystem.seed = lsystem.system.symbol("F");
			}
//			lsystem.system.parameter("angle").value = 15;
//			lsystem.system.parameter("size").value = .5f;
			lsystem.generator.begin();
			lsystem.generator.current.position.setZero();
			lsystem.generator.current.orientation.set(Vector3.X);
			lsystem.generator.current.up.set(Vector3.Y);
			lsystem.generator.current.scale.set(1,1,1);
			lsystem.generator.current.color.set(Color.WHITE);
			
			lsystem.generator.current.renderer = renderer;
			lsystem.generator.generate(lsystem.seed, depth);
		}
	}
}

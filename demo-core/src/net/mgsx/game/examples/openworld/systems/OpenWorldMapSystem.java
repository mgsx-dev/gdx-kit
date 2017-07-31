package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.examples.openworld.components.CellDataComponent;

@Storable("ow.map")
@EditableSystem
public class OpenWorldMapSystem extends EntitySystem {

	@Inject OpenWorldEnvSystem env;
	@Inject OpenWorldManagerSystem manager;
	@Inject OpenWorldGeneratorSystem generator;
	
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	@ShaderInfo(vs="shaders/map.vert", fs="shaders/map.frag", inject=false)
	public static class MapShader extends ShaderProgramManaged
	{
		@Uniform public transient float waterLevel;
	}
	@Editable public MapShader shader = new MapShader();
	@Editable public float mapSize = .3f;
	@Editable public Vector2 mapOffset = new Vector2(10, 10);
	
	private GameScreen screen;
	
	public OpenWorldMapSystem(GameScreen screen) {
		super(GamePipeline.RENDER_TOOLS-1);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		float mx = mapOffset.x;
		float my = mapOffset.y;
		float ms = mapSize * Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float mw = ms / manager.getLogicWidth();
		float mh = ms / manager.getLogicHeight();
		
		shader.waterLevel = (env.waterLevel / generator.scale + 1) * .5f;
		
		if(shader.begin()){
			if(batch != null) batch.dispose();
			batch = new SpriteBatch(4, shader.program());
			if(renderer != null) renderer.dispose();
			renderer = new ShapeRenderer();
		}
		batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		batch.disableBlending();
		batch.begin();
		for(int y=0 ; y<manager.getLogicHeight() ; y++)
		{
			for(int x=0 ; x<manager.getLogicWidth() ; x++)
			{
				Entity entity = manager.getHeightMaps()[(manager.getLogicHeight() - 1 - y) * manager.getLogicHeight() + x];
				if(entity != null){
					CellDataComponent cdc = CellDataComponent.components.get(entity);
					batch.draw(cdc.data.infoTexture, mx + mw*x, my + y*mh, mw, mh);
				}
			}
			
		}
		batch.end();
		
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		float renderSize = mw * manager.getLogicWidth();
		
		float camX = renderSize * (manager.viewPoint.x / manager.worldCellScale - manager.getLogicOffsetX()) / manager.getLogicWidth();
		float camY = renderSize - renderSize * (manager.viewPoint.y / manager.worldCellScale - manager.getLogicOffsetY())/ manager.getLogicHeight();
		
		Color c1 = new Color(Color.WHITE);
		c1.a = 0.5f;
		Color c2 = new Color(Color.WHITE);
		c2.a = 0.0f;
		
		float fov = screen.camera instanceof PerspectiveCamera ? ((PerspectiveCamera)screen.camera).fieldOfView : 90;
		float farScale = 0.05f;
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		renderer.setTransformMatrix(
				renderer.getTransformMatrix()
				.idt()
				.translate(mx + camX, my + camY, 0)
				.rotateRad(Vector3.Z, -MathUtils.PI * .5f + MathUtils.atan2(-screen.camera.direction.z, screen.camera.direction.x)));
		renderer.begin(ShapeType.Filled);
		renderer.triangle(
				0, 0, 
				farScale * screen.camera.far * MathUtils.cosDeg(fov), farScale * screen.camera.far * MathUtils.sinDeg(fov),
				farScale * -screen.camera.far * MathUtils.cosDeg(fov), farScale * screen.camera.far * MathUtils.sinDeg(fov), 
				c1, c2, c2);
		//renderer.line(mx + camX, my + camY, mx + camX + camDirX * mw, my + camY + camDirY * mh, Color.BLACK, Color.WHITE);
		renderer.end();
	}
}

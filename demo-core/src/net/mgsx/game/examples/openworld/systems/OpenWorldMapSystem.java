package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

public class OpenWorldMapSystem extends EntitySystem {

	@Inject OpenWorldManagerSystem manager;
	
	private FrameBuffer mapFbo;

	private int logicalOffsetX;
	private int logicalOffsetY;
	
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	private ShaderProgram shader;
	
	// TODO just attach texture to entities !
	private Array<Texture> heightMapTextures = new Array<Texture>();

	private GameScreen screen;
	
	public OpenWorldMapSystem(GameScreen screen) {
		super(GamePipeline.RENDER_TOOLS-1);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		shader = new ShaderProgram(Gdx.files.internal("shaders/map.vert"), Gdx.files.internal("shaders/map.frag"));
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// Render to texture first time or if logic position has changed.
		if(mapFbo == null || manager.getLogicOffsetX() != logicalOffsetX || manager.getLogicOffsetY() != logicalOffsetY) {
			
			for(Texture t : heightMapTextures) t.dispose();
			heightMapTextures.clear();
			
			mapFbo = new FrameBuffer(Format.RGBA8888, 256, 256, true);
			
			float minHeight = -manager.scale;
			float maxHeight = manager.scale;
			float heightScale = 1.f / (maxHeight - minHeight);
			
			OrthographicCamera camera = new OrthographicCamera(manager.logicSize, manager.logicSize);
			camera.position.set(0,0,-1);
			camera.direction.set(0, 0, 1);
			camera.up.set(Vector3.Y);
			camera.near = .1f;
			camera.far = 3000f; //maxHeight - minHeight + 1;
			camera.update();
			
			SpriteBatch batch = new SpriteBatch(4, shader);
			
			mapFbo.begin();
			
			Entity [] heightMaps = manager.getHeightMaps();
			Color pixel = new Color();
			for(Entity entity : heightMaps) {
				HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
				Pixmap pixmap = new Pixmap(hfc.width, hfc.height, Format.RGBA8888);
				for(int y=0 ; y<hfc.height ; y++){
					for(int x=0 ; x<hfc.width ; x++){
						pixmap.drawPixel(x, y, Color.rgba8888(pixel.set((hfc.values[y*hfc.width+x] - minHeight) * heightScale, 0f, 0f, 1)));
					}
				}
				
				Texture heightTexture = new Texture(pixmap);
				
				batch.setProjectionMatrix(camera.combined);
				float fx = -manager.logicSize/2f + hfc.position.x / manager.worldCellScale - manager.getLogicOffsetX();
				float fy = manager.logicSize/2f-1 - hfc.position.z / manager.worldCellScale + manager.getLogicOffsetY();
				
				batch.setTransformMatrix(new Matrix4().setToTranslation(fx, fy, 0));
				batch.begin();
				batch.draw(heightTexture, 0, 0, 1, 1);
				batch.end();
				
				heightMapTextures.add(heightTexture);
			}
			
			mapFbo.end();
			
			logicalOffsetX = manager.getLogicOffsetX();
			logicalOffsetY = manager.getLogicOffsetY();
		}
		
		float renderSize = 64;
		
		// Render map
		// render some dots (camera origin)
		batch.disableBlending();
		batch.begin();
		batch.draw(mapFbo.getColorBufferTexture(), 10+renderSize, 10+renderSize, -renderSize, -renderSize);
//		for(int i=0 ; i<heightMapTextures.size ; i++){
//			int x = i%manager.logicSize;
//			int y = manager.logicSize-i/manager.logicSize-1;
//			batch.draw(heightMapTextures.get(i), 400 + x * 32, 100 + y * 32, 32, 32);
//		}
		batch.end();
		
		float camX = renderSize * (manager.viewPoint.x / manager.worldCellScale - manager.getLogicOffsetX()) / manager.getLogicWidth();
		float camY = renderSize - renderSize * (manager.viewPoint.y / manager.worldCellScale - manager.getLogicOffsetY())/ manager.getLogicHeight();
		Vector2 vdir = new Vector2(screen.camera.direction.x, -screen.camera.direction.z).nor();
		float camDirX = vdir.x;
		float camDirY = vdir.y;
		
		renderer.begin(ShapeType.Line);
		renderer.line(10 + camX, 10 + camY, 10 + camX + camDirX * 10, 10 + camY + camDirY * 10, Color.BLUE, Color.YELLOW);
		renderer.end();
	}
}

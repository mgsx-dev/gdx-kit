package net.mgsx.game.examples.openworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.utils.VirtualGrid;

public class VirtualGridGUITest extends Game {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 950;
		new LwjglApplication(new VirtualGridGUITest(), config);
	}
	
	private static class Cell{
		Rectangle r = new Rectangle();
		Color c = new Color();
		int d;
	}
	
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private VirtualGrid<Cell> vg;
	private Array<Cell> cells = new Array<Cell>();
	private Vector2 position = new Vector2();
	private int vgWidth = 3, vgHeight = 5, vgMarginWidth = 2, vgMarginHeight = 3;
	private float vgScale = 32f;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		renderer = new ShapeRenderer();
		
		vg = new VirtualGrid<VirtualGridGUITest.Cell>() {
			
			@Override
			protected void dispose(Cell cell) {
				cells.removeValue(cell, true);
			}
			
			@Override
			protected Cell create(float worldX, float worldY) {
				Cell cell = new Cell();
				cell.c.set(Color.RED);
				cell.c.a = MathUtils.random(.3f, .6f);
				cell.r.set(worldX, worldY, worldCellScale, worldCellScale);
				cells.add(cell);
				return cell;
			}
			
			@Override
			protected void exit(Cell cell) {
				float a = cell.c.a;
				cell.c.set(Color.BLUE);
				cell.c.a = a;
			}
			
			@Override
			protected void enter(Cell cell) {
				float a = cell.c.a;
				cell.c.set(Color.GREEN);
				cell.c.a = a;
			}

			@Override
			protected void update(Cell cell, int distance) {
				if(distance <= 2)
					cell.d = distance;
				else
					cell.d = 0;
			}
		};
		
		vg.resize(vgWidth, vgHeight, vgMarginWidth, vgMarginHeight, vgScale);
		
		position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
	}
	
	@Override
	public void render() {
		
		if(Gdx.input.isTouched()){
			float targetX = Gdx.input.getX();
			float targetY = Gdx.graphics.getHeight() - Gdx.input.getY();
			position.set(targetX, targetY);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.C)) vg.clear();
		if(Gdx.input.isKeyJustPressed(Input.Keys.S)) vg.shrink();
		
		boolean changed = false;
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
			if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
				if(vgMarginWidth > 0) vgMarginWidth--;
				changed = true;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
				vgMarginWidth++;
				changed = true;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
				if(vgMarginHeight > 0) vgMarginHeight--;
				changed = true;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
				vgMarginHeight++;
				changed = true;
			}
		}else{
			if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
				if(vgWidth > 1) vgWidth--;
				changed = true;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
				vgWidth++;
				changed = true;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
				if(vgHeight > 1) vgHeight--;
				changed = true;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
				vgHeight++;
				changed = true;
			}
		}
		
		if(changed){
			vg.resize(vgWidth, vgHeight, vgMarginWidth, vgMarginHeight, vgScale);
		}
		
		vg.update(position.x, position.y);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Filled);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		for(Cell cell : cells){
			float s = cell.d * 2 + 1;
			renderer.setColor(cell.c);
			renderer.rect(cell.r.x + s, cell.r.y + s, cell.r.width - 2*s, cell.r.height - 2*s);
		}
		
		renderer.setColor(Color.YELLOW);
		renderer.x(position, 32);
		
		renderer.end();
	}
	
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
	}
}

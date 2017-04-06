package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class GridDebugSystem extends EntitySystem
{
	@Editable
	public float size = 1f;
	
	@Editable
	public int maxLines = 60;
	
	@Editable
	public float opacity = .3f;
	
	@Editable
	public boolean snap = true;
	
	@Inject protected DebugRenderSystem renderer;
	
	private EditorScreen editor;
	
	private BoundingBox box = new BoundingBox();
	private Vector3 center = new Vector3();
	
	
	public GridDebugSystem(EditorScreen editor) {
		super(GamePipeline.RENDER_DEBUG);
		this.editor = editor;
	}



	@Override
	public void update(float deltaTime) 
	{
		renderer.shapeRenderer.begin(ShapeType.Line);
		renderer.shapeRenderer.setColor(Color.BROWN);
		renderer.shapeRenderer.getColor().a = opacity;
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
		editor.getEditorCamera().camera().project(center.setZero());
		editor.getEditorCamera().camera().unproject(box.min.set(0,Gdx.graphics.getHeight(),center.z));
		editor.getEditorCamera().camera().unproject(box.max.set(Gdx.graphics.getWidth(), 0, center.z));
		
		int ymin = MathUtils.ceil(box.min.y / size);
		int ymax = MathUtils.ceil(box.max.y / size);
		
		int inc = MathUtils.floor(Math.abs(ymax - ymin) / maxLines) + 1;
		
		float newSize = size * inc;
		
		ymin = MathUtils.ceil(box.min.y / newSize);
		ymax = MathUtils.ceil(box.max.y / newSize);

		for(int y=ymin ; y<ymax ; y++){
			float gy = y * newSize;
			renderer.shapeRenderer.line(box.min.x, gy, box.max.x, gy);
		}
		int xmin = MathUtils.ceil(box.min.x / size);
		int xmax = MathUtils.ceil(box.max.x / size);
		for(int x=xmin ; x<xmax ; x++){
			float gx = x * newSize;
			renderer.shapeRenderer.line(gx, box.min.y, gx, box.max.y);
		}
		
		
		renderer.shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
	}
}

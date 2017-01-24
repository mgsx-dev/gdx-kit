package net.mgsx.game.examples.war.editors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.mgsx.game.examples.war.model.History;

public class HistoryWidget extends Widget
{
	private ShapeRenderer renderer = new ShapeRenderer();
	private History history;
	private int min, max;

	public HistoryWidget(History history, int min, int max) {
		super();
		this.history = history;
		this.min = min;
		this.max = max;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BROWN);
		renderer.rect(getX(), getY(), getWidth(), getHeight());
		renderer.end();
		
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.RED);
		
		for(int i=1 ; i<history.history.size ; i++){
			int v0 = history.history.get(i-1);
			int v1 = history.history.get(i);
			float nx0 = (float)(i-1) / (float)(history.history.size -1);
			float nx1 = (float)i / (float)(history.history.size -1);
			float ny0 = (float)(v0 - history.min) / (float)(history.max - history.min);
			float ny1 = (float)(v1 - history.min) / (float)(history.max - history.min);
			
			float x0 = getX() + nx0 * getWidth();
			float x1 = getX() + nx1 * getWidth();
			float y0 = getY() + ny0 * getHeight();
			float y1 = getY() + ny1 * getHeight();
			renderer.line(x0, y0, x1, y1);
		}
		
		renderer.end();
		
		
		
		batch.begin();
	}
}

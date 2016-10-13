package net.mgsx.box2d.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class SkinFactory {

	public static Skin createSkin()
	{
		ShapeRenderer renderer = new ShapeRenderer();
		FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, 512, 512, false);
		fbo.bind();
		// render some basic shapes ?
		int s = drawSquare(renderer, Color.DARK_GRAY, Color.LIGHT_GRAY, 2);
		TextureRegion region = new TextureRegion(fbo.getColorBufferTexture(), 0, 0, s, s);
		NinePatch patch = grid(region);
		renderer.translate(s+1, 0, 0);
		int s2 = drawSquare(renderer, Color.RED, Color.LIGHT_GRAY, 2);
		TextureRegion region2 = new TextureRegion(fbo.getColorBufferTexture(), 0 + s + 1, 0, s2, s2);
		NinePatch patch2 = grid(region2);
		
		fbo.end();
		
		Drawable bg = new NinePatchDrawable(patch);
		
		BitmapFont font = new BitmapFont();
		
		Skin skin = new Skin();
		
		LabelStyle lbs = new LabelStyle();
		lbs.font = font;
		skin.add("default", lbs);
		
		TextButtonStyle tbs = new TextButtonStyle();
		tbs.font = font;
		tbs.up = new NinePatchDrawable(patch);
		tbs.checked = new NinePatchDrawable(patch2);
		skin.add("default", tbs);
		
		ScrollPaneStyle sps = new ScrollPaneStyle();
		skin.add("default", sps);
		
		ListStyle ls = new ListStyle();
		ls.selection = bg;
		ls.background = bg;
		ls.font = font;
		skin.add("default", ls);
		
		SelectBoxStyle sbs = new SelectBoxStyle();
		sbs.scrollStyle = sps;
		sbs.listStyle = ls;
		sbs.background = bg;
		sbs.font = font;
		skin.add("default", sbs);
		
		
		return skin;
		
	}
	
	private static int drawSquare(ShapeRenderer renderer, Color fill, Color stroke, int strokeSize)
	{
		// TODO
		// draw stroke as full rect
		// then draw fill as smaller rect
		
		int size = 1 + (strokeSize + 1) * 2;
		
		renderer.begin(ShapeType.Filled);
		renderer.setColor(stroke);
		renderer.rect(0, 0, size, size);
		renderer.setColor(fill);
		renderer.rect(strokeSize, strokeSize, size-2*strokeSize, size-2*strokeSize);
		renderer.end();
		
		return size;
	}
	
	private static NinePatch grid(TextureRegion region){
		TextureRegion[] r = new TextureRegion[9];
		for(int y=0 ; y<3 ; y++)
			for(int x=0 ; x<3 ; x++)
				r[y*3+x] = new TextureRegion(region.getTexture(), 
						region.getRegionX() + (region.getRegionWidth() * x) / 3, 
						region.getRegionY() + (region.getRegionHeight() * y) / 3,
						region.getRegionWidth() / 3,
						region.getRegionHeight() / 3);
		return new NinePatch(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
	}
}

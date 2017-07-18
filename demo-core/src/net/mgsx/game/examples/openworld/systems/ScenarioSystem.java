package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;

@EditableSystem
public class ScenarioSystem extends EntitySystem
{

	private String introText;
	BitmapFont font;
	private Batch batch;
	private Label label;

	public ScenarioSystem() {
		super(GamePipeline.HUD);
	}
	
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		OpenWorldModel.load();
		introText = OpenWorldModel.root.child("quests").child("intro").child(0).asString();
		
		font = new BitmapFont(Gdx.files.internal("openworld/game_font.fnt"));
		LabelStyle style = new LabelStyle(font, Color.WHITE);
		label = new Label(introText, style );
		batch = new SpriteBatch();
	}
	
	@Override
	public void update(float deltaTime) {
		if(GdxAI.getTimepiece().getTime() > 10) return;
		batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		batch.begin();
		label.validate();
		font.setColor(1f,1f,.1f, MathUtils.sin(GdxAI.getTimepiece().getTime() * 1) * 0.5f + 0.5f);
		font.draw(batch, introText,( Gdx.graphics.getWidth()-Gdx.graphics.getWidth()/4f)/2f, Gdx.graphics.getHeight() / 2f, 0, introText.length(), Gdx.graphics.getWidth()/4f, Align.center, true);
		batch.end();
	}
}

package net.mgsx.game.examples.openworld.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.examples.openworld.model.OpenWorldModel;

public class ScenarioView extends Table
{
	BitmapFont font;
	private Label label;

	public ScenarioView(Skin skin)
	{
		super(skin);
		
		OpenWorldModel.load(); // TODO lazy ?
		String introText = OpenWorldModel.map.root().child("quests").child("intro").child(0).asString();
		
		// TODO put font in skin or create a game skin for open world
		font = new BitmapFont(Gdx.files.internal("openworld/game_font.fnt"));
		LabelStyle style = new LabelStyle(font, Color.WHITE);
		label = new Label(introText, style);
		label.setAlignment(Align.center, Align.center);
		label.setWrap(true);
		add(label).width(Gdx.graphics.getWidth()/2);
		
		label.getColor().a = 0;
		label.addAction(Actions.sequence(Actions.alpha(1, 4)));
	}
}

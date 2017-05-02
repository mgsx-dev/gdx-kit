package net.mgsx.game.examples.tactics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.examples.tactics.logic.CharacterBattle;
import net.mgsx.game.examples.tactics.model.FactionDef;

public class CharacterUI extends Table
{
	private Label life, turns;
	private CharacterBattle character;
	
	public CharacterUI(Skin skin, CharacterBattle character) {
		super(skin);
		this.character = character;
		
		Table table = this;
		
		table.setTouchable(Touchable.enabled);
		
		table.setBackground("default-window");
		
		table.defaults().pad(5);
		table.add(character.def.id);
		
		FactionDef faction = character.def.model.factions.get(character.def.faction);
		
		Image img = IconsHelper.image(faction.icon);
		img.setColor(Color.BLACK);
		
		table.add(img).row();
		
		life = new Label("", skin);
		turns = new Label("", skin);
		
		table.add(life).row();
		table.add(turns).row();
		
		table.setTransform(true);
		table.setUserObject(character);
		
		update();
	}
	
	public void update()
	{
		life.setText(String.format("life %d / %d", character.life, character.def.life));
		turns.setText(String.format("turns %d", (int)character.turns));
	}

	public void setTarget(boolean isTarget) {
		
		setBackground(isTarget ? "invert-window" : "default-window");
		// setColor(isTarget ? Color.WHITE : Color.WHITE);
	}

	public Vector2 getLifePosition() {
		return localToStageCoordinates(new Vector2(life.getX() + life.getWidth()/2, life.getY() + life.getHeight()/2));
	}
	
}

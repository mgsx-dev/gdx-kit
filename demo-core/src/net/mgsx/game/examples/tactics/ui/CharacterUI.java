package net.mgsx.game.examples.tactics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.examples.tactics.logic.CardBattle;
import net.mgsx.game.examples.tactics.logic.CharacterBattle;
import net.mgsx.game.examples.tactics.logic.EffectBattle;
import net.mgsx.game.examples.tactics.model.FactionDef;

public class CharacterUI extends Table
{
	private Label life, turns;
	private CharacterBattle character;
	
	private Table fxTable;
	private Table cardTable;
	private ObjectMap<EffectBattle, EffectUI> effectWidgets = new ObjectMap<EffectBattle, EffectUI>();
	private ObjectMap<CardBattle, CardUI> cardWidgets = new ObjectMap<CardBattle, CardUI>();
	
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
		img.setColor(Color.valueOf(faction.color));
		
		table.add(img).row();
		
		life = new Label("", skin);
		turns = new Label("", skin);
		
		table.add(life).row();
		table.add(turns).row();
		
		// TODO add current effects ...
		fxTable = new Table(skin);
		cardTable = new Table(skin);
		
		table.add(fxTable).row();
		table.add(cardTable);
		
		for(CardBattle card : character.cards){
			CardUI ui = new CardUI(card, skin);
			cardTable.add(ui).fill().row();
			cardWidgets.put(card, ui);
			ui.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// TODO ...
				}
			});
		}
		
		table.setTransform(true);
		table.setUserObject(character);
		
		update();
	}
	
	public void update()
	{
		life.setText(String.format("life %d / %d", character.life, character.def.life));
		turns.setText(String.format("turns %d", (int)character.turns));
		
		for(Entry<CardBattle, CardUI> ui : cardWidgets.entries()){
			ui.value.update();
		}
	}

	public void setTarget(boolean isTarget) {
		
		setBackground(isTarget ? "invert-window" : "default-window");
		// setColor(isTarget ? Color.WHITE : Color.WHITE);
	}

	public Vector2 getLifePosition() {
		return localToStageCoordinates(new Vector2(life.getX() + life.getWidth()/2, life.getY() + life.getHeight()/2));
	}

	public void onEffectApply(EffectBattle fx) 
	{
		if(fx.turns <= 0){
			Actor actor = effectWidgets.get(fx, null);
			if(actor != null){
				actor.remove();
				effectWidgets.remove(fx);
			}
		}else{
			if(!effectWidgets.containsKey(fx)){
				EffectUI ui = new EffectUI(fx, getSkin());
				effectWidgets.put(fx, ui);
				fxTable.add(ui);
			}
			else{
				effectWidgets.get(fx).update();
			}
		}
		
	}

	public void onDie() 
	{
		// effectWidgets.clear();
		// fxTable.clear();
		Image img = IconsHelper.image("ra-death-skull");
		img.setBounds(0, 0, getWidth(), getHeight());
		img.setColor(1,1,1, .5f);
		this.addActor(img);
	}

	public void removeEffect(EffectBattle fx) {
		if(effectWidgets.get(fx) != null) effectWidgets.get(fx).remove();
	}
	
}

package net.mgsx.game.examples.tactics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.examples.tactics.logic.CardBattle;

public class CardUI extends Table
{
	private CardBattle card;
	private Label button;
	
	public CardUI(final CardBattle card, Skin skin) {
		super(skin);
		this.card = card;
		
		add(new Image(skin.getDrawable("default-window-header-left")));
		
		button = new Label(card.def.id, skin);
		button.setColor(Color.GRAY);
//		button.setStyle(new LabelStyle(button.getStyle()));
//		button.getStyle().background = skin.getDrawable("default-window-body-right");
		add(button).expand().fill();
		
		button.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(card.turns <= 0){
					fire(new ChangeEvent());
				}
			}
		});
	}
	
	public void update(){
		if(card.turns > 0)
			button.setText(card.def.id + " (" + (int)card.turns + ")");
		else
			button.setText(card.def.id);
	}
	
	public Actor getButton() {
		return button;
	}
	
}

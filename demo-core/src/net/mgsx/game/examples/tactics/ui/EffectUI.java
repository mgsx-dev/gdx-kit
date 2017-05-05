package net.mgsx.game.examples.tactics.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.examples.tactics.logic.EffectBattle;

public class EffectUI extends Stack
{
	private Image img;
	private Label label;
	private EffectBattle fx;
	public EffectUI(EffectBattle fx, Skin skin) {
		this.fx = fx;
		
		if(fx.life > 0){
			img = IconsHelper.image("ra-health");
			img.setColor(.5f, 1f, .5f, 1);
		}else if(fx.life < 0){
			img = IconsHelper.image("ra-bone-bite");
			img.setColor(.5f, .5f, .2f, 1);
		}else if(fx.protection > 0){
			img = IconsHelper.image("ra-shield");
			img.setColor(1f, 1f, .2f, 1);
		}else if(fx.protection < 0){
			img = IconsHelper.image("ra-broken-shield");
			img.setColor(1f, .6f, .2f, 1);
		}else{
			img = IconsHelper.image("ra-help");
			img.setColor(1f, 1f, 1f, 1);
		}
		
		label = new Label(String.valueOf((int)fx.turns), skin);
		label.setAlignment(Align.center);
		
		add(img);
		add(label);
	}
	
	public void update(){
		label.setText(String.valueOf((int)fx.turns));	
	}
}

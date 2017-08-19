package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;

import net.mgsx.game.examples.openworld.model.Compound;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class SecretsView extends Table
{

	private OpenWorldGameSystem gameSystem;
	
	Table details;
	
	public SecretsView(Skin skin, Engine engine) {
		super(skin);
		
		gameSystem = engine.getSystem(OpenWorldGameSystem.class);
		
		details = new Table(skin);
		
		Table list = new Table(getSkin());
		list.defaults().fill();
		for(String uid : gameSystem.player.knownSecrets){
			list.add(createSecretButton(uid)).row();
		}
		add("Secrets Discovered").colspan(2).expandX().row();
		add(new ScrollPane(list,  getSkin())).expand().minHeight(100).minWidth(100).fill();
		add(new ScrollPane(details, getSkin())).expand().fill();
	}

	private Actor createSecretButton(final String uid) {
		String label = OpenWorldModel.name(uid);
		TextButton bt = new TextButton(label, getSkin());
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showSecret(uid);
			}
		});
		return bt;
	}

	protected void showSecret(String uid) {
		details.clear();
		
		Label summary = new Label(OpenWorldModel.description(uid), getSkin());
		summary.setWrap(true);
		
		details.add(OpenWorldModel.name(uid)).row();
		details.add(summary).width(Gdx.graphics.getWidth() / 2).row();
		
		details.add("Requirements").expandX().left().row();
		Compound compound = OpenWorldModel.fusion(uid);
		ObjectMap<String, Integer> map = compound.getMap();
		Array<String> keys = map.keys().toArray();
		Sort.instance().sort(keys);
		for(String key : keys){
			String txt = map.get(key) + "x " + OpenWorldModel.name(key);
			details.add(txt).row();
		}
	}
}

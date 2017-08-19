package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class StatisticsView extends Table
{

	private OpenWorldGameSystem gameSystem;
	
	public StatisticsView(Skin skin, Engine engine) {
		super(skin);
		
		setBackground("default-window");
		
		gameSystem = engine.getSystem(OpenWorldGameSystem.class);
		
		Table global = new Table(skin);
		global.defaults().fill();
		global.add("you walked "); global.add(gameSystem.player.distanceWalk + "m").row();
		global.add("you swim "); global.add(gameSystem.player.distanceSwim + "m").row();
		global.add("you fly "); global.add(gameSystem.player.distanceFly + "m").row();
		global.add("you earned "); global.add(gameSystem.player.experience + " XP points").row();
		global.add("you reach level "); global.add(""+gameSystem.player.level).row();
		global.add("you survived "); global.add(gameSystem.player.timePlay + " seconds").row();
		global.add("you slept "); global.add(gameSystem.player.timeSleep + " seconds").row();
		
		// TODO format better some strings
		
		// TODO percent of quest/secrets completed ?
		
		for(Entry<String, ObjectMap<String, Integer>> act : gameSystem.player.actions){
			for(Entry<String, Integer> item : act.value){
				global.add("You " + act.key + " " + item.key);
				global.add(item.value + " times").row();
			}
		}
		add("All Statistics").colspan(2).expandX().row();
		add(new ScrollPane(global,  getSkin())).expand().maxHeight(Gdx.graphics.getHeight()/2).fill();
	}

}

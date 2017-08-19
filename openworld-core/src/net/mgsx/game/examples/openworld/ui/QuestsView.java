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

import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class QuestsView extends Table
{
	private OpenWorldGameSystem gameSystem;
	
	Table tableQuest;
	
	public QuestsView(Skin skin, Engine engine) {
		super(skin);
		
		// display all quests order by :
		// locked first then model order ?
		// unlocked then model order ?
		
		gameSystem = engine.getSystem(OpenWorldGameSystem.class);
		
		tableQuest = new Table(skin);
		
		Table questList = new Table(getSkin());
		questList.defaults().fill();
		for(String questUID : gameSystem.player.pendingQuests){
			questList.add(createQuestButton(questUID, false)).row();
		}
		for(String questUID : gameSystem.player.completedQuests){
			questList.add(createQuestButton(questUID, true)).row();
		}
		add("All Quests").colspan(2).expandX().row();
		add(new ScrollPane(questList,  getSkin())).expand().fill();
		add(new ScrollPane(tableQuest, getSkin())).expand().fill();
	}

	private Actor createQuestButton(final String questUID, final boolean complete) {
		String label = OpenWorldModel.quest(questUID).name();
		if(complete){
			label += " (completed)";
		}
		TextButton bt = new TextButton(label, getSkin());
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showQuest(questUID, complete);
			}
		});
		return bt;
	}

	protected void showQuest(String questUID, boolean complete) {
		tableQuest.clear();
		
		Label summary = new Label(OpenWorldModel.quest(questUID).summary(), getSkin());
		summary.setWrap(true);
		
		tableQuest.add(OpenWorldModel.quest(questUID).name()).row();
		tableQuest.add(summary).width(Gdx.graphics.getWidth() / 2).row();
		
		
		int xp = OpenWorldModel.quest(questUID).xp();
		if(xp != 0) tableQuest.add("Reward XP: " + xp).row();
		
		Array<String> items = OpenWorldModel.quest(questUID).items();
		if(items.size > 0){
			String txt = "New items :";
			for(String item : items){
				txt += " " + OpenWorldModel.name(item);
			}
			tableQuest.add(txt).row();
		}
		
		Array<String> knowledges = OpenWorldModel.quest(questUID).knowledges();
		if(knowledges.size > 0){
			String txt = "New knowledges :";
			for(String knowledge : knowledges){
				txt += " " + OpenWorldModel.name(knowledge);
			}
			tableQuest.add(txt).row();
		}
		// TODO other rewards here ...
		
		tableQuest.add(complete ? "COMPLETED" : "").row();;
	}
	
}

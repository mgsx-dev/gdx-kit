package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

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
		
		tableQuest.add(OpenWorldModel.quest(questUID).name()).row();
		tableQuest.add(OpenWorldModel.quest(questUID).summary()).row();
		
		tableQuest.add(complete ? "COMPLETED" : "").row();;
	}
	
}

package net.mgsx.game.examples.openworld.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.model.OpenWorldGameEventManager;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class QuestStatusPopup extends Table
{
	public QuestStatusPopup(Skin skin, final OpenWorldGameSystem gameSystem, final String qid, boolean complete) {
		super(skin);
		
		setFillParent(true);
		setTouchable(Touchable.enabled);
		
		if(complete){
			
			add("Quest completed : " + OpenWorldModel.quest(qid).name()).row();
			
			// add(OpenWorldModel.quest(qid).summary()).row();
			
			int xp = OpenWorldModel.quest(qid).xp();
			if(xp != 0) add("Reward XP: " + xp).row();
			
			Array<String> items = OpenWorldModel.quest(qid).items();
			if(items.size > 0){
				String txt = "New items :";
				for(String item : items){
					txt += " " + OpenWorldModel.name(item);
				}
				add(txt).row();
			}
			
			Array<String> knowledges = OpenWorldModel.quest(qid).knowledges();
			if(knowledges.size > 0){
				String txt = "New knowledges :";
				for(String knowledge : knowledges){
					txt += " " + OpenWorldModel.name(knowledge);
				}
				add(txt).row();
			}
			// TODO other rewards here ...
			
			
			add("Tap to continue");
			
			getColor().a = 0;
			
			addAction(Actions.sequence(Actions.alpha(1, 2)));
			
			addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent event, float x, float y) {
					addAction(Actions.sequence(
							Actions.alpha(0, 2),
							//ack(gameSystem, qid),
							Actions.removeActor()));
				}
			});
		}
		else
		{
			
			add("New quest available : " + OpenWorldModel.quest(qid).name());
			
			getColor().a = 0;
			
			addAction(Actions.sequence(
					Actions.alpha(1, 2),
					Actions.delay(3),
					Actions.alpha(0, 2),
					//ack(gameSystem, qid),
					Actions.removeActor()));
		}
		
		
		
	}
	
	private static Action ack(final OpenWorldGameEventManager manager, final String qid){
		return Actions.run(new Runnable() {
			
			@Override
			public void run() {
				manager.questAck(qid);
			}
		});
	}
	
}

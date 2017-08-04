package net.mgsx.game.examples.openworld.ui;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.model.OpenWorldModel;

public class QuestStatusPopup extends Table
{
	private Array<Action> pendingActions = new Array<Action>();
	
	public QuestStatusPopup(Skin skin) {
		super(skin);
		
		setTouchable(Touchable.disabled);
		setVisible(false);
	}
	
	public void pushQuestStatus(final String qid, boolean complete)
	{
		pendingActions.add(Actions.sequence(
			Actions.alpha(0),
			buildUIAction(qid, complete),
			Actions.visible(true),
			Actions.alpha(1, 2),
			Actions.delay(3),
			Actions.alpha(0, 2),
			Actions.visible(false)
		));
	}
	
	@Override
	public void act(float delta) {
		if(getActions().size == 0 && pendingActions.size > 0){
			addAction(pendingActions.removeIndex(0));
		}
		super.act(delta);
	}
	
	private Action buildUIAction(final String qid, final boolean complete) {
		return Actions.run(new Runnable() {
			@Override
			public void run() {
				displayQuestStatus(qid, complete);
			}
		});
	}

	private void displayQuestStatus(final String qid, boolean complete)
	{
		clearChildren();
		
		if(complete)
		{
			add("Quest completed : " + OpenWorldModel.quest(qid).name()).row();
		}
		else
		{
			add("New quest available : " + OpenWorldModel.quest(qid).name());
		}
	}
}

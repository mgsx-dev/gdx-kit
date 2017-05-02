package net.mgsx.game.examples.tactics.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class SmoothTable extends Table
{
	private SequenceAction sequence;
	
	public SmoothTable() {
		super();
	}

	public SmoothTable(Skin skin) {
		super(skin);
	}
	
	public <T extends Actor> Cell<T> addSmooth(T actor, float duration) {
		if(!hasActions()){
			sequence = new SequenceAction();
			addAction(sequence);
		}
		final Cell<T> cell = add(actor);
		actor.getColor().a = 0;
		AlphaAction a = Actions.fadeIn(duration);
		a.setTarget(actor);
		sequence.addAction(a);
		
		return cell;
	}
	
	public boolean removeActorSmooth(Actor actor, float duration) 
	{
		if(getCell(actor) == null) return false;
		if(!hasActions()){
			sequence = new SequenceAction();
			addAction(sequence);
		}
		AlphaAction a = Actions.fadeOut(duration);
		a.setTarget(actor);
		sequence.addAction(a);
		sequence.addAction(Actions.removeActor(actor));
		return true;
	}
}

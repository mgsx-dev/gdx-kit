package net.mgsx.game.examples.tactics.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class JoinAction extends Action
{
	
	private Actor[] actors;

//	public JoinAction(Actor [] actors) {
//		this.actors = actors;
//	}
	
	public JoinAction(Actor ... actors) {
		this.actors = actors;
	}
	
	@Override
	public boolean act(float delta) {
		for(Actor actor : actors){
			if(actor.hasActions()) return false;
		}
		return true;
	}

}

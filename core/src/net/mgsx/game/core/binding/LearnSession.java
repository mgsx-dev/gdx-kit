package net.mgsx.game.core.binding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class LearnSession implements LearnListener {

	private Array<Actor> overlayWidgets = new Array<Actor>();
	private Stage stage;
	private Skin skin;
	private Learnable current;
	public void startLearning(Stage stage, Skin skin)
	{
		this.stage = stage;
		this.skin = skin;
		
		// TODO instrument widgets
		buildOverlay(stage.getRoot());
		
		// TODO start learners
		for(Learner learner : BindingManager.learners){
			learner.startLearning(this);
		}
		Gdx.input.setInputProcessor(new InputMultiplexer(Gdx.input.getInputProcessor(), stage));
	}
	
	private void buildOverlay(Actor actor)
	{
		if(!actor.isVisible()) return;
		if(actor instanceof Learnable)
		{
			final Learnable learnable = (Learnable)actor;
			// TODO build
			Binding bind = BindingManager.getBinding(learnable.bindKey());
			final TextButton bt = new TextButton(bind == null ? "---" : bind.command, skin);
			overlayWidgets.add(bt);
			stage.addActor(bt);
			Vector2 a = actor.localToStageCoordinates(new Vector2());
			Vector2 b = actor.localToStageCoordinates(new Vector2(actor.getWidth(), actor.getHeight()));
			
			bt.setBounds(a.x, a.y, b.x-a.x, b.y-a.y);
			
			bt.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					current = learnable;
				}
			});
			
		}
		if(actor instanceof Group){
			for(Actor child : ((Group) actor).getChildren()){
				buildOverlay(child);
			}
		}
		
	}

	@Override
	public void onCommand(Learner learner, String command) {
		if(command != null)
		{
			if(current == null) return;
			// TODO store binding !
			Binding b = new Binding();
			b.command = command;
			b.accessor = current.accessorToBind();
			b.target = current.bindKey();
			learner.bind(b);
			BindingManager.setBindings(b);
		}else{
			for(Learner l : BindingManager.learners){
				l.stopLearning();
			}
			// hide overlays
			for(Actor actor : overlayWidgets) actor.remove();
			overlayWidgets.clear();
		}
		
	}
}

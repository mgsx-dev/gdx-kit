package net.mgsx.game.core.binding;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class BindingManager {

	final private static ObjectMap<String, Binding> bindings = new ObjectMap<String, Binding>();
	
	static final Array<Learner> learners = new Array<Learner>(new Learner[]{new KeyboardLearner()});

	public static void setBindings(Binding b) {
		bindings.put(b.target, b);
	}

	public static Binding getBinding(String target) {
		return bindings.get(target);
	}

	public static void clear() {
		for(Entry<String, Binding> entry : BindingManager.bindings)
		{
			for(Learner learner : BindingManager.learners){
				learner.unbind(entry.value);
			}
		}
		BindingManager.bindings.clear();
		
	}

	public static void applyBindings(Binding b, Stage stage) {
		bindings.put(b.target, b);
		
		bindActor(stage.getRoot());
		
		for(Learner learner : BindingManager.learners){
			learner.bind(b);
		}
	}

	private static void bindActor(Actor actor) {
		if(actor instanceof Learnable)
		{
			final Learnable learnable = (Learnable)actor;
			Binding bind = BindingManager.getBinding(learnable.bindKey());
			if(bind != null){
				bind.accessor = learnable.accessorToBind();
			}
		}
		if(actor instanceof Group){
			for(Actor child : ((Group) actor).getChildren()){
				bindActor(child);
			}
		}
	}

	public static ObjectMap<String, Binding> bindings() {
		return bindings;
	}

}

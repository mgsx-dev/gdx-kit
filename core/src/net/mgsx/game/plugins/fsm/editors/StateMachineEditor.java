package net.mgsx.game.plugins.fsm.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.plugins.fsm.StateMachineEditorPlugin;
import net.mgsx.game.plugins.fsm.components.EntityState;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;

public class StateMachineEditor implements EntityEditorPlugin {
	
	
	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		final StateMachineComponent smc = StateMachineComponent.components.get(entity);
		
		final SelectBox<EntityState> selector = new SelectBox<EntityState>(skin);
		final SelectBox<EntityState> initSelector = new SelectBox<EntityState>(skin);
		
		Array<EntityState> states = new Array<EntityState>();
		for(EntityState state : StateMachineEditorPlugin.allStates()){
			if(state.getFamily().matches(entity)){
				states.add(state);
			}
		}
		
		selector.setItems(states);
		initSelector.setItems(states);
		
		TextButton btSet = new TextButton("Set State", skin);
		btSet.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				smc.fsm.changeState(selector.getSelected());
			}
		});
		
		initSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				smc.initialState = initSelector.getSelected();
				smc.fsm.setInitialState(initSelector.getSelected());
			}
		});
		
		Table table = new Table(skin);
		table.add(btSet);
		table.add(selector).row();
		table.add("Initial");
		table.add(initSelector).row();
		
		return table;
	}

}

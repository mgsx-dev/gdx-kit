package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;

public class CraftingView extends Table
{
	public static interface Callback{
		void onComplete(Array<OpenWorldElement> selection);
	}

	private Table backpackList;
	private Table buildingList;
	private Array<OpenWorldElement> selection = new Array<OpenWorldElement>();

	public CraftingView(Skin skin, Engine engine, final Callback callback) {
		super(skin);
		
		add("Backpack");
		add("Building");
		row();
		
		backpackList = new Table(skin);
		buildingList = new Table(skin);
		add(new ScrollPane(backpackList, skin)).expand();
		add(new ScrollPane(buildingList, skin)).expand();
		row();
		
		TextButton btCancel = new TextButton("Cancel", skin);
		TextButton btOK = new TextButton("Build", skin);
		add(btCancel);
		add(btOK);
		row();
		
		btCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				callback.onComplete(null);
			}
		});
		btOK.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(selection.size > 0){
					callback.onComplete(selection);
				}
			}
		});
		
		for(final OpenWorldElement item : engine.getSystem(OpenWorldGameSystem.class).backpack){
			appendToBackpackList(item);
		}
	}

	private void appendToBackpackList(final OpenWorldElement item) {
		final TextButton bt = new TextButton(OpenWorldModel.name(item.type), getSkin());
		backpackList.add(bt).fillX().row();
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				selection.add(item);
				appendToBuildingList(item);
				bt.remove();
			}
		});
	}

	private void appendToBuildingList(final OpenWorldElement item) {
		final TextButton bt = new TextButton(OpenWorldModel.name(item.type), getSkin());
		buildingList.add(bt).fillX().row();
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				selection.removeValue(item, true);
				appendToBackpackList(item);
				bt.remove();
			}
		});
	}
	
}

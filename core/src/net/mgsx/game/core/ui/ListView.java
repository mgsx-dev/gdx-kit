package net.mgsx.game.core.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

abstract public class ListView<T> extends Table
{
	public ListView(Array<T> items, Skin skin) {
		super(skin);
		setItems(items);
	}
	
	public void setItems(Array<T> items){
		clearChildren();
		createHeader(this);
		row();
		add().height(10);
		row();
		
		for(T item : items){
			createItem(this, item);
			row();
		}
	}
	
	abstract protected void createHeader(Table parent);
	
	abstract protected void createItem(Table parent, T item);

	public void addItem(T item) 
	{
		createItem(this, item);
		row();
	}

}

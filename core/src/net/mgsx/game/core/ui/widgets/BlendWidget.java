package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;

public class BlendWidget implements FieldEditor
{
	public static final BlendWidget instance = new BlendWidget();
	
	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		final SelectBox<String> sbType = new SelectBox<String>(skin);
		sbType.setItems("zero", "one", "src", "dst", "cst");
		
		
		final SelectBox<String> sbChannel = new SelectBox<String>(skin);
		sbChannel.setItems("color", "alpha");
		
		
		final CheckBox cbInverse = new CheckBox("invert", skin);
		
		Table table = new Table(skin);
		table.add(sbType);
		table.add(sbChannel);
		table.add(cbInverse);
		
		ChangeListener listener = new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				accessor.set(computeMode(sbType.getSelected(), sbChannel.getSelected(), cbInverse.isChecked()));
			}
		};
		
		sbType.addListener(listener);
		sbChannel.addListener(listener);
		cbInverse.addListener(listener);
		
		return table;
	}

	protected int computeMode(String type, String channel, boolean invert) 
	{
		if(type.equals("zero")) return GL20.GL_ZERO;
		if(type.equals("one")) return GL20.GL_ONE;
		if(type.equals("src")) 
			if(channel.equals("color")) return invert ? GL20.GL_ONE_MINUS_SRC_COLOR : GL20.GL_SRC_COLOR;
			else if(channel.equals("alpha")) return invert ? GL20.GL_ONE_MINUS_SRC_ALPHA : GL20.GL_SRC_ALPHA;
		if(type.equals("dst")) 
			if(channel.equals("color")) return invert ? GL20.GL_ONE_MINUS_DST_COLOR : GL20.GL_DST_COLOR;
			else if(channel.equals("alpha")) return invert ? GL20.GL_ONE_MINUS_DST_ALPHA : GL20.GL_DST_ALPHA;
		if(type.equals("cst")) 
			if(channel.equals("color")) return invert ? GL20.GL_ONE_MINUS_CONSTANT_COLOR : GL20.GL_CONSTANT_COLOR;
			else if(channel.equals("alpha")) return invert ? GL20.GL_ONE_MINUS_CONSTANT_ALPHA : GL20.GL_CONSTANT_ALPHA;
		return GL20.GL_ZERO;
	}
	

	
}

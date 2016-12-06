package net.mgsx.game.core.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.ui.accessors.Accessor;

public class DefaultFieldEditor implements FieldEditor
{

	@Override
	public Actor create(Entity entity, Accessor accessor, Skin skin) 
	{
		// TODO move all EntityEditor switches here
		return null;
	}

}

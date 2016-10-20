package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.plugins.EntityEditorPlugin;

public class Box2DBodyEditorPlugin implements EntityEditorPlugin
{

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		// TODO create contextual editor ...
		
		return new Label("nothing here ...", skin);
	}

}

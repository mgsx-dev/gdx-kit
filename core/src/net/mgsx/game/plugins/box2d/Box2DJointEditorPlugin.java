package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.box2d.model.Box2DJointModel;

public class Box2DJointEditorPlugin implements EntityEditorPlugin
{

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		return new EntityEditor(entity.getComponent(Box2DJointModel.class), skin);
	}

}

package net.mgsx.game.core.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.game.core.ui.accessors.Accessor;

/**
 * Custom field editor, used to edit particular type.
 * 
 * @author mgsx
 *
 */
public interface FieldEditor
{
	public Actor create(Entity entity, Accessor accessor, Skin skin);
}

package net.mgsx.plugins.g3d.animation;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.mgsx.core.plugins.EntityEditorPlugin;
import net.mgsx.core.ui.EntityEditor;

public class TextureAnimationEditor implements EntityEditorPlugin
{

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		final TextureAnimationComponent anim = entity.getComponent(TextureAnimationComponent.class);
		
		EntityEditor editor = new EntityEditor(skin);
		editor.setEntity(anim);
		return editor;
	}
}

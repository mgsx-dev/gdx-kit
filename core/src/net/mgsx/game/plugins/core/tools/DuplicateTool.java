package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.plugins.Initializable;

public class DuplicateTool extends SelectTool
{
	public DuplicateTool(EditorScreen editor) {
		super("Duplicate", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// skip super implementation ...
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return super.touchDragged(screenX, screenY, 0); // move has drag ... TODO ugly
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.D && shift())
		{
			Array<Entity> duplicates = new Array<Entity>();
			for(Entity entity : editor.selection)
			{
				Entity newEntity = duplicateEntity(editor, entity);
				duplicates.add(newEntity);
			}
			editor.selection.clear();
			editor.selection.addAll(duplicates);
			editor.invalidateSelection();
			moving = true;
			prev = new Vector2(Gdx.input.getX(), Gdx.input.getY());
			return true;
		}
		return false;
	}
	
	public static Entity duplicateEntity(EditorScreen editor, Entity base){
		Entity newEntity = editor.createEntity();
		editor.entityEngine.addEntity(newEntity);
		for(Component component : base.getComponents()){
			if(component instanceof Duplicable)
			{
				Component newComponent = ((Duplicable) component).duplicate();
				if(newComponent instanceof Initializable){
					((Initializable) newComponent).initialize(editor.entityEngine, newEntity);
				}
				newEntity.add(newComponent);
			}
		}
		return newEntity;
	}
	
	
}

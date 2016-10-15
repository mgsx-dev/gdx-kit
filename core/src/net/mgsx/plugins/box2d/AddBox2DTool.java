package net.mgsx.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import net.mgsx.core.Editor;
import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.tools.EditorTool;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;
import net.mgsx.plugins.box2d.tools.NoTool;

public class AddBox2DTool extends EditorTool
{
	private WorldItem worldItem;
	private BodyItem bodyItem;
	private Entity entity;
	
	public AddBox2DTool(Editor editor, WorldItem worldItem) {
		super("Box2Dl", editor);
		this.worldItem = worldItem;
	}
	
	@Override
	protected void activate() {
		
		entity = editor.currentEntity();
		
		BodyItem data = bodyItem = new BodyItem(entity, "id", null, null);
		
		entity.add(data);
		
		editor.setSelection(entity); // here we set !
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){ // TODO use click tool
			create(unproject(screenX, screenY));
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	protected void create(Vector2 position) {
		
		BodyDef def = worldItem.settings.body();
		Body body = worldItem.world.createBody(def);
		body.setUserData(entity);
		bodyItem.def = def;
		bodyItem.body = body;
		
		entity.add(new Movable(new BodyMove(body)));

		bodyItem.body.setTransform(position.x, position.y, bodyItem.body.getAngle());
		
		entity = null;
	}
}

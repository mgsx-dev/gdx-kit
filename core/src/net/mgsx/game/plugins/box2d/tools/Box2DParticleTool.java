package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.ExpiryComponent;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

public class Box2DParticleTool  extends Tool
{
	@Inject SelectionSystem selection;
	
	@Editable public float life = 3;
	@Editable public float rate = 4;
	@Editable public float expiryDelay = 2;
	
	private float time;
	
	private Vector2 position;
	private Vector2 direction;

	private Entity base;
	
	public Box2DParticleTool(EditorScreen editor) {
		super("Particle", editor);
		activator = Family.all(Box2DBodyModel.class).get();
	}
	
	@Override
	protected void activate() {
		super.activate();
		base = selection.selected();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			position = unproject(screenX, screenY);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(position != null){
			direction = unproject(screenX, screenY).sub(position);
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			position = null;
			direction = null;
			return true;
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if(position != null && direction != null)
		{
			time += Gdx.graphics.getDeltaTime();
			float period = 1.f / rate;
			if(time > period){
				time -= period;
				Entity entity = EntityHelper.clone(getEngine(), base);
				ExpiryComponent expiry = getEngine().createComponent(ExpiryComponent.class);
				expiry.time = expiryDelay;
				entity.add(expiry);
				Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
				if(physics != null)
				{
					physics.body.setTransform(position, 0);
					physics.body.setLinearVelocity(new Vector2(direction).scl(5f));
				}
			}
		}
	}

}

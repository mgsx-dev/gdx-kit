package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.plugins.Movable;

public class SpriteMove extends Movable
{
	private Sprite sprite;
	
	
	public SpriteMove(Sprite sprite) {
		this.sprite = sprite;
	}



	@Override
	public void move(Entity entity, Vector3 deltaWorld) 
	{
		sprite.setX(sprite.getX() + deltaWorld.x);
		sprite.setY(sprite.getY() + deltaWorld.y);
	}
	
	@Override
	public void moveTo(Entity entity, Vector3 pos) {
		sprite.setPosition(pos.x, pos.y);
	}
	
	@Override
	public void getPosition(Entity entity, Vector3 pos) {
		pos.set(sprite.getX(), sprite.getY(), 0);
	}


}
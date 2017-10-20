package net.mgsx.game.blueprint.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Link {
	public Portlet src, dst;
	public Vector2 srcPosition = new Vector2();
	public Vector2 dstPosition = new Vector2();

	public Link(Portlet src, Portlet dst) {
		super();
		this.src = src;
		this.dst = dst;
	}

	public Vector2 getSrcPosition() {
		if(src.actor != null){
			getCenter(src.actor, srcPosition);
		}
		return srcPosition;
	}

	public Vector2 getDstPosition() {
		if(dst.actor != null){
			getCenter(dst.actor, dstPosition);
		}
		return dstPosition;
	}
	
	private static Vector2 a = new Vector2(), b = new Vector2();
	
	public static Vector2 getCenter(Actor actor, Vector2 v){
		
		a.setZero();
		b.set(actor.getWidth(), actor.getHeight());
	
		return v.set((a.x+b.x)/2, (a.y+b.y)/2);
	}

}

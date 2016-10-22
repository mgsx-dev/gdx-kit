package net.mgsx.game.plugins.box2dold.model;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;

public class Items
{
	public Array<JointItem> joints = new Array<JointItem>();
	public Array<Box2DBodyModel> bodies = new Array<Box2DBodyModel>();
	public  Array<SpriteItem> sprites = new Array<SpriteItem>();
	
	public void addAll(Items items) {
		joints.addAll(items.joints);
		bodies.addAll(items.bodies);
		sprites.addAll(items.sprites);
	}
	public void clear() {
		joints.clear();
		bodies.clear();
		sprites.clear();
	}
}
package net.mgsx.plugins.box2d.model;

import com.badlogic.gdx.utils.Array;

public class Items
{
	public Array<JointItem> joints = new Array<JointItem>();
	public Array<BodyItem> bodies = new Array<BodyItem>();
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
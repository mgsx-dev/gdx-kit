package net.mgsx.box2d.editor.model;

import com.badlogic.gdx.utils.Array;

public class Items
{
	public Array<JointItem> joints = new Array<JointItem>();
	public Array<BodyItem> bodies = new Array<BodyItem>();
	public void addAll(Items items) {
		joints.addAll(items.joints);
		bodies.addAll(items.bodies);
	}
	public void clear() {
		joints.clear();
		bodies.clear();
	}
}
package net.mgsx.game.plugins.box2d.model;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;

public class Box2DJointModel implements Component
{
	public String id;
	public JointDef def;
	public Joint joint;
	public Box2DJointModel() {
	}
	public Box2DJointModel(String id, JointDef def, Joint joint) {
		super();
		this.id = id;
		this.def = def;
		this.joint = joint;
	}
	
}
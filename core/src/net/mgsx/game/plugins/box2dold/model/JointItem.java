package net.mgsx.game.plugins.box2dold.model;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;

public class JointItem
{
	public String id;
	public JointDef def;
	public Joint joint;
	public JointItem(String id, JointDef def, Joint joint) {
		super();
		this.id = id;
		this.def = def;
		this.joint = joint;
		if(joint != null) joint.setUserData(this);
	}
	
}
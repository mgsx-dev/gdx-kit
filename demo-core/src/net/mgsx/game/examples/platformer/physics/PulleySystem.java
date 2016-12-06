package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class PulleySystem extends IteratingSystem
{
	public PulleySystem() {
		super(Family.all(Box2DJointModel.class, PulleyComponent.class, G3DModel.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// TODO
		G3DModel pulleyModel = entity.getComponent(G3DModel.class);
		
		Box2DJointModel jm = entity.getComponent(Box2DJointModel.class);
		
		if(pulleyModel != null && jm != null)
		{
			float ratio = 0;
			
			if(jm.joint instanceof PulleyJoint){
				PulleyJoint pulley = (PulleyJoint)jm.joint;
				
				
				//pulley.
				
				float lenA = pulley.getGroundAnchorA().sub(pulley.getBodyA().getPosition()).len();
				ratio = lenA / pulley.getLength1();
				// System.out.println(ratio);
			}
			
			ModelInstance mi = pulleyModel.modelInstance; //.nodes.get(0);
			
			Node chainNode = mi.nodes.get(0);
			Node rightNode = mi.nodes.get(1);
			Node leftNode = mi.nodes.get(2);
			
			rightNode.rotation.setFromAxis(Vector3.X, 90).mul(new Quaternion(Vector3.Y, ratio * 120));
			leftNode.rotation.setFromAxis(Vector3.X, 90).mul(new Quaternion(Vector3.Y, ratio * 120));
			// leftNode.rotation.mul(new Quaternion(Vector3.Y, 1));
			
			NodePart part = chainNode.parts.first();
			if(part != null){
				
				TextureAttribute ta = (TextureAttribute)part.material.get(TextureAttribute.Diffuse);
				
				ta.textureDescription.vWrap = TextureWrap.ClampToEdge;
				//ta.offsetU += deltaTime * component.uPerSec;
				ta.offsetV = 0.12f * ratio - 0.135f;
				ta.scaleV =1;
				// ta.offsetV += deltaTime * 0.1f;
			}
			
//			node.rotation.set(new Quaternion().mul(new Quaternion(Vector3.X, 90)).mul(new Quaternion(Vector3.Y, 180)));
//			node.scale.set(1,1,1);
			// rightNode.calculateTransforms(true);
			mi.calculateTransforms();
		}
		
	}

}

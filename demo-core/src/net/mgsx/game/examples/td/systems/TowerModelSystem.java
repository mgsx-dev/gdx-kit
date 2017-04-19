package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Aiming;
import net.mgsx.game.examples.td.components.Tower;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class TowerModelSystem extends IteratingSystem
{
	public TowerModelSystem() {
		super(Family.all(G3DModel.class, Tower.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		G3DModel model = G3DModel.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
//		Speed speed = Speed.components.get(entity);
		model.modelInstance.transform.idt();
		
		model.modelInstance.transform.rotate(Vector3.X, -90);
		model.modelInstance.transform.translate(transform.position.x, transform.position.y, transform.depth);
		//model.modelInstance.transform.rotate(Vector3.X, 90);
		
//		model.modelInstance.transform.rotateRad(Vector3.Y, (float)( - Math.atan2(transform.derivative.y,- transform.derivative.x)) );
//		model.modelInstance.transform.rotate(Vector3.Y, 90);
		
		model.modelInstance.transform.rotateRad(Vector3.Z, -(float)( Math.atan2(transform.normal.x, -transform.normal.y)) );
		model.modelInstance.transform.rotateRad(Vector3.Y, -(float)( Math.acos(transform.normal.z)) );
		
		model.modelInstance.transform.rotate(Vector3.X, 90);
		
		
//		model.modelInstance.transform.rotate(Vector3.Y, -90);
//			model.modelInstance.transform.rotate(Vector3.Y, 90);
//		if(transform.derivative.len2() > 0 && transform.normal.len2() > 0)
//			model.modelInstance.transform.mul(new Matrix4().setToLookAt(transform.derivative, Vector3.Z));
		
		// model.modelInstance.transform.rotate(Vector3.X, -90);
		
		
//		model.modelInstance.transform.rotate(Vector3.Z,  90 - MathUtils.radiansToDegrees * (float)Math.acos(transform.normal.z));
//
//		model.modelInstance.transform.rotate(Vector3.X, -90);
		
		
		
		float s = 0.2f;
		model.modelInstance.transform.scale(s, s, s);
//		if(model.animationController.current == null){
//			model.animationController.animate("Armature|walk", -1, null, 0);
//		}
//		model.animationController.current.speed = speed.current * 8;
		
		Aiming aiming = Aiming.components.get(entity);
		
		Material mat = model.modelInstance.getMaterial("M_Light");
		if(mat != null){
//			ColorAttribute emissive = (ColorAttribute)mat.get(ColorAttribute.Emissive);
//			if(emissive == null){
//				mat.set(emissive = new ColorAttribute(ColorAttribute.Emissive, Color.WHITE));
//			}
//			FloatAttribute alpha = (FloatAttribute)mat.get(FloatAttribute.AlphaTest);
//			if(alpha == null){
//				mat.set(alpha = new FloatAttribute(FloatAttribute.AlphaTest, 0));
//			}
//			alpha.value = 0f;
//			
//			emissive.color.set(Color.WHITE);
			ColorAttribute diffuse = (ColorAttribute)mat.get(ColorAttribute.Diffuse);
			diffuse.color.a = 0.1f;
			BlendingAttribute blend = (BlendingAttribute)mat.get(BlendingAttribute.Type);
			if(blend != null) mat.remove(BlendingAttribute.Type);
					
//			BlendingAttribute blend = (BlendingAttribute)mat.get(BlendingAttribute.Type);
//			if(blend == null){
//				blend = new BlendingAttribute(0.5f);
//				mat.set(blend);
//			}
//			blend.blended = true;
//			blend.sourceFunction = GL20.GL_SRC_ALPHA;
//			blend.destFunction = GL20.GL_ZERO;
//			blend.opacity = .5f;
			
		}
		
		
		Node head = model.modelInstance.getNode("head");
		if(head != null){
			if(aiming != null){
				head.rotation.idt();
				head.rotation.setEulerAngles(aiming.angle, -90, 0);
				head.calculateTransforms(true);
			}
		}
		Node body = model.modelInstance.getNode("body");
		if(body != null){
			body.rotation.idt();
			body.rotation.setEulerAngles(transform.angle, -90, 0);
			body.calculateTransforms(true);
		}
		
	}
}

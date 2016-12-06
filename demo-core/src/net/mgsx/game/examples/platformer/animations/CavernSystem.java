package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.platformer.logic.PlayerComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class CavernSystem extends IteratingSystem
{

	private GameScreen engine;
	
	public CavernSystem(GameScreen engine) {
		super(Family.all(G3DModel.class, CavernComponent.class).get(), GamePipeline.LOGIC);
		this.engine = engine;
	}
	
	@Override
	public void update(float deltaTime) 
	{
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		CavernComponent scolo = entity.getComponent(CavernComponent.class);
		G3DModel model = entity.getComponent(G3DModel.class);
		if(!scolo.ready){
			Material material = model.modelInstance.getMaterial("scolopandre");
			((BlendingAttribute)material.get(BlendingAttribute.Type)).blended = true;
			scolo.insectPathEffect = (TextureAttribute)material.get(TextureAttribute.Diffuse);
			scolo.insectPathEffect.textureDescription.uWrap = TextureWrap.ClampToEdge;
			scolo.insectPathEffect.textureDescription.vWrap = TextureWrap.Repeat;
			scolo.bigEyes = new Array<Node>();
			scolo.bigEyes.add(model.modelInstance.getNode("bigeye1", true));
			scolo.bigEyes.add(model.modelInstance.getNode("bigeye2", true));
			scolo.bigEyes.add(model.modelInstance.getNode("bigeye3", true));
			
			scolo.ctrl = new AnimationController(model.modelInstance);
			scolo.ctrl.setAnimation("Icosphere.003Action", -1);
			scolo.smalllEyes = new Array<Node>();
			for(int i=1 ; i<=5 ; i++){
				scolo.smalllEyes.add(model.modelInstance.getNode("eye" + i, true)); // TODO how to animate all ... special controller ?
			}
			
			scolo.ready = true;
		}
		scolo.ctrl.update(deltaTime);
		
		ImmutableArray<Entity> players = engine.entityEngine.getEntitiesFor(Family.all(PlayerComponent.class).get());
		if(players.size() > 0){
			Vector2 target = players.first().getComponent(PlayerComponent.class).physics.body.getPosition();
			// TODO set rotation
			for(Node bigEye : scolo.bigEyes){
				Vector3 p = bigEye.globalTransform.getTranslation(new Vector3());
				bigEye.rotation.idt();
				p.sub(target.x, target.y, 0).nor();
				p.x = -p.x; // TODO why ?
				bigEye.rotation.mul(new Quaternion().setFromCross(p , Vector3.X));
//				bigEye.rotation.mul(new Quaternion().setFromAxis(Vector3.Y, -90));
				//bigEye.rotation.mulLeft(new Quaternion().setFromAxis(Vector3.Y, 180));
			//	 bigEye.rotation.mulLeft(new Quaternion().setFromAxis(Vector3.X, 90));
			}
		}
		
		scolo.time += deltaTime;
		scolo.insectPathEffect.offsetU += deltaTime * 0.7f;
		if(scolo.insectPathEffect.offsetU > 2) scolo.insectPathEffect.offsetU -= 7;
		int frame = (int)(scolo.time * 30f);
		scolo.insectPathEffect.offsetV = frame / 9.f + 2f/ (9 * 64);
		
		model.modelInstance.calculateTransforms();
	}

	

}

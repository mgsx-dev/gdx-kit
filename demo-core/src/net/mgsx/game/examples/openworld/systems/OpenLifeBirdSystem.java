package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.WildLifeComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldPathBuilder;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

@Storable("ow.birds")
@EditableSystem
public class OpenLifeBirdSystem extends IteratingSystem
{
	@Asset("openworld/ptero.g3dj")
	public Model birdModel;
	
	@Inject OpenWorldCameraSystem cameraSystem;
	@Inject OpenWorldManagerSystem manager;
	
	@Editable public int maxBirds = 10;
	private int numBirds; // required because entity list doesn't refresh
	
	@Editable public float speed = 0.1f;
	@Editable public float distance = 5f;
	@Editable public float offset = 10f;
	
	@Editable public transient boolean debugSplines = false;
	
	private OpenWorldPathBuilder pathBuilder = new OpenWorldPathBuilder();

	public OpenLifeBirdSystem() {
		super(Family.all(WildLifeComponent.class).get(), GamePipeline.RENDER);
	}
	
	
	public void update(float deltaTime) 
	{
		// if bird count not reached, lets create a bird
		// define its trajectory : around camera
		// 
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		while(numBirds < maxBirds)
		{
			Entity bird = getEngine().createEntity();
			G3DModel model = getEngine().createComponent(G3DModel.class);
			model.modelInstance = new ModelInstance(birdModel);
			model.animationController = new AnimationController(model.modelInstance);
			model.culling = false;
			
			// String [] anims = {"Armature|fly-full", "Armature|fly-low", "Armature|fly-speed" /*,"Armature|fly-transition"*/};
			
			model.animationController.setAnimation("Armature|fly-full", -1, 1.5f, null);
			
			bird.add(model);
			
			PathComponent path = getEngine().createComponent(PathComponent.class);
			Vector3[] controlPoints = new Vector3[10];
			pathBuilder.set(manager, 30, distance, offset);
			
			// TODO maybe create farest from camera ...
			Vector3 init = pathBuilder.randomXZ(new Vector3(), camera.position);
			pathBuilder.createPath(controlPoints, init); 

			
			path.path = new CatmullRomSpline<Vector3>(controlPoints , false);
			path.length = path.path.approxLength(100);
			bird.add(path);
			
			WildLifeComponent life = getEngine().createComponent(WildLifeComponent.class);
			life.time = 0;
			life.speed = MathUtils.random(0.1f, 1);
			bird.add(life);
			
			if(debugSplines){
				bird.add(getEngine().createComponent(SplineDebugComponent.class));
			}
			
			getEngine().addEntity(bird);
			numBirds++;
		}
		
		super.update(deltaTime);
	}

	private Vector3 position = new Vector3();
	private Vector3 direction = new Vector3();
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		WildLifeComponent life = WildLifeComponent.components.get(entity);
		G3DModel model = G3DModel.components.get(entity);
		PathComponent path = PathComponent.components.get(entity);
		
		life.time += deltaTime * speed * life.speed / path.length;
		
		if(life.time > 1){
			
			getEngine().removeEntity(entity);
			numBirds--;
			return; // TODO free to pool
		}
		
		path.path.valueAt(position, life.time);
		path.path.derivativeAt(direction, life.time);
		
		model.modelInstance.transform.setToRotation(Vector3.Z, direction.nor().scl(-1));
		model.modelInstance.transform.setTranslation(position);
		model.modelInstance.calculateTransforms();
	}

	
}

package net.mgsx.game.examples.circuit.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.Kit;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.circuit.model.CircuitCar;
import net.mgsx.game.examples.circuit.model.CircuitModel;
import net.mgsx.game.examples.circuit.model.CircuitTemplates;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class CircuitRenderSystem extends EntitySystem
{
	private ShapeRenderer renderer;
	private Vector3 [] wireframeVertcies;
	
	@Inject
	protected POVModel pov; 
	
	@Inject
	public CircuitModel circuit;
	
	private static Vector3 v0 = new Vector3();
	private static Vector3 v1 = new Vector3();
	
	private CatmullRomSpline<Vector3> catmullrom;
	private Vector3 tangent = new Vector3();
	private Vector3 normal = new Vector3();
	private Vector3 binormal = new Vector3();
	private float[] grid;
	private Rectangle gridRect;
	private int gridCols;
	private int gridRows;
	private float playerU, playerV, cameraU, cameraV;
	private float playerSpeed;
	private Entity playerEntity;
	private boolean inGameCamera = false;
	private CameraInputController cameraControl;
	private PerspectiveCamera outCamera;
	
	public CircuitRenderSystem() {
		super(GamePipeline.RENDER);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		renderer = new ShapeRenderer();
		
		outCamera = new PerspectiveCamera();
		cameraControl = new CameraInputController(outCamera);
		Kit.inputs.addProcessor(cameraControl);
	}

	@Override
	public void update(float deltaTime) {
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
			inGameCamera = !inGameCamera;
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.F5)){
			wireframeVertcies = null;
			circuit.dots.clear();
			circuit.cars.clear();
			circuit.tracks.clear();
			circuit.invalidate();
		}
		
		
		float camSpeed = 10; // XXX 10
		float carSpeed = .03f;
		
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			playerV = Math.max(playerV - deltaTime * 1f, -1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			playerV = Math.min(playerV + deltaTime * 1f, 1);
		}
		
		
		if(circuit.dots.size == 0){
			Model model = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("circuit/cars.g3dj"));
			CircuitTemplates.simple(circuit, model);
			for(CircuitCar car : circuit.cars){
				Entity e = getEngine().createEntity();
				G3DModel m = getEngine().createComponent(G3DModel.class);
				m.modelInstance = car.model;
				e.add(m);
				getEngine().addEntity(e);
			}
			playerEntity = getEngine().createEntity();
			G3DModel m = getEngine().createComponent(G3DModel.class);
			m.modelInstance = new ModelInstance(model, "car.1", false);
			playerEntity.add(m);
			getEngine().addEntity(playerEntity);
		}
		
		int dotCount = circuit.dots.size;
		if(wireframeVertcies == null || dotCount != wireframeVertcies.length){
			if(dotCount > 0){
				wireframeVertcies = new Vector3[dotCount];
				for(int i=0 ; i<dotCount ; i++){
					Vector2 p = circuit.dots.get(i);
					// wireframeVertcies[i] = new Vector3(p, 1150 * (MathUtils.sinDeg(2 * 360f * (float)i/(float)dotCount) + 1) /2); // XXX MathUtils.random(10));
				
				
					wireframeVertcies[i] = new Vector3(p, 150 * (MathUtils.sinDeg(4 * 360f * (float)i/(float)dotCount) + 1) /2);
				}
				
				if(dotCount >= 4){
					catmullrom = new CatmullRomSpline<Vector3>();
					catmullrom.set(wireframeVertcies, true);
					
					// TODO compute obstacles (sphere)
					
					// TODO make grid with an extra bock size * 5
					float blocSize = 10;
					int extrablocs = 20;
					
					Rectangle r = new Rectangle();
					r.setCenter(wireframeVertcies[0].x, wireframeVertcies[0].y);
					for(int i=1 ; i<wireframeVertcies.length ; i++){
						r.merge(wireframeVertcies[i].x, wireframeVertcies[i].y);
					}
					r.x += r.width / 2;
					r.y += r.height / 2;
					r.width += blocSize * extrablocs;
					r.height += blocSize * extrablocs;
					r.x -= r.width / 2;
					r.y -= r.height / 2;
					gridRect = new Rectangle(r.x, r.y, blocSize, blocSize);
					
					gridCols = (int)(r.width / blocSize) + 1;
					gridRows = (int)(r.height / blocSize) + 1;
					
					grid = new float[gridRows * gridCols];
					for(int i=0 ; i<grid.length ; i++) grid[i] = 200f;
					
					// TODO collide with spline (path marching by sphere < blocSize
					float sphereRadius = blocSize * .48f;
					Vector3 spherePos = new Vector3();
					float marchT = 0;
					// float totalLength = catmullrom.approxLength(wireframeVertcies.length * 10);
					while(marchT < 1){
						catmullrom.valueAt(spherePos, marchT);
						catmullrom.derivativeAt(tangent, marchT);
						float tanLen = tangent.len();
						float spd = sphereRadius / tanLen;
						int sx = (int)((spherePos.x - gridRect.x) / blocSize);
						int sy = (int)((spherePos.y - gridRect.y) / blocSize);
						for(int y = 0 ; y<2 ; y++){
							for(int x = 0 ; x<2 ; x++){
								int gx = sx + x;
								int gy = sy + y;
								if(gx >= 0 && gx < gridCols && gy >= 0 && gy < gridRows){
									int gIndex = (gy * gridCols) + gx;
									grid[gIndex] = Math.min(grid[gIndex], spherePos.z);
								}
							}
						}
						marchT += spd;
					}
					
					for(int i=0 ; i<grid.length ; i++) if(grid[i] >= 200f)
						grid[i] = MathUtils.random(20f);
					
//					int passes = 2;
//					for(int i=0 ; i<passes ; i++)
//						for(int y=0 ; y<gridRows ; y++)
//							for(int x=0 ; x<gridCols ; x++){
//								float sum = 0;
//								int count = 0;
//								for(int dy=0 ; dy<=2 ; dy++)
//									for(int dx=0 ; dx<=2 ; dx++){
//										int gx = (x + dx - 1 + gridCols) % gridCols;
//										int gy = (y + dy - 1 + gridRows) % gridRows;
//										if(gx>=0 && gx<gridCols && gy>=0 && gy<gridRows){
//											sum += grid[gy*gridCols+gx];
//											count++;
//										}
//									}
//								grid[y*gridCols+x] = sum / count;
//							}
					
				}else{
					catmullrom = null;
				}
				
				
			}else{
				wireframeVertcies = null;
				catmullrom = null;
			}
		}
		
		if(dotCount > 0){
			renderer.setProjectionMatrix(pov.camera.combined);
			if(dotCount > 1){
				renderer.begin(ShapeType.Line);
				renderer.setColor(Color.BLACK);
				for(int i=1 ; i<dotCount ; i++){
					renderer.line(wireframeVertcies[i-1], wireframeVertcies[i]);
				}
				
				if(grid != null){
					renderer.setColor(.3f, .5f, .2f, 1f);
					for(int y=0 ; y<gridRows ; y++){
						float fy = gridRect.y + (y-.5f) * gridRect.width;
						for(int x=0 ; x<gridCols ; x++){
							float fx = gridRect.x + (x-.5f) * gridRect.width;
							float fz = grid[y*gridCols+x];
							if(fz < 400) renderer.line(fx, fy, 0, fx, fy, fz);
							else renderer.line(fx, fy, 0, fx, fy, 10);
						}
					}
					
				}
				float len = .2f;
				float rw = 45;
				float rh = 1f;
				
				if(catmullrom != null){
					
					circuit.update(catmullrom, deltaTime);
					
					// float time = (Gdx.graphics.getFrameId() / 960f) % 1f;
					catmullrom.derivativeAt(tangent, playerU).nor();
					normal.set(Vector3.Z).crs(tangent).nor();
					binormal.set(tangent).crs(normal).nor();
					
					// TODO draw a red box with simulated sprite ... (with speed ...) and follow cam
					catmullrom.valueAt(v0, playerU);
					
					G3DModel playerModel = G3DModel.components.get(playerEntity);
					playerModel.modelInstance.transform.idt().translate(v0).rotate(Vector3.X, 90).mul(new Matrix4().setToLookAt(tangent, binormal)); // 
					playerModel.modelInstance.calculateTransforms();
					
					renderer.setColor(Color.PURPLE);
					renderer.line(new Vector3(v0).mulAdd(normal, (playerV ) * rw*len), new Vector3(v0).mulAdd(normal, (playerV ) *rw*len).mulAdd(binormal, 1));
					
					if(inGameCamera){
						
						if(Gdx.input.isKeyPressed(Input.Keys.A))
							playerSpeed = MathUtils.lerp(playerSpeed, carSpeed, deltaTime * 1.3f);
						else
							playerSpeed = MathUtils.lerp(playerSpeed, 0f, deltaTime * 3);
						
						playerU += playerSpeed / catmullrom.derivativeAt(tangent, playerU).len();
						playerU = (playerU +1f)%1f;
						
						playerV = MathUtils.clamp(MathUtils.lerp(playerV, tangent.cpy().crs(pov.camera.direction).dot(pov.camera.up), deltaTime * .005f), -1, 1);
						
						cameraU = MathUtils.lerp(cameraU, playerU, deltaTime * camSpeed);
						cameraV = MathUtils.lerp(cameraV, playerV, deltaTime * 50);
						
						catmullrom.valueAt(pov.camera.position, cameraU).mulAdd(binormal, 1).mulAdd(normal, cameraV* rw*len);
						pov.camera.direction.set(tangent);
						pov.camera.up.set(binormal);
						pov.camera.update();
					}
					else
					{
						outCamera.up.set(Vector3.Z);
						outCamera.update();
						cameraControl.update();
						// pov.camera.position.set(outCamera.position);
						pov.camera.direction.set(outCamera.direction);
						pov.camera.up.set(outCamera.up);
						pov.camera.update();
					}
					
					
					v0.set(wireframeVertcies[0]);
					int segments = dotCount * 30;
					
					Vector3 pA = new Vector3();
					Vector3 pB = new Vector3();
					Vector3 pC = new Vector3();
					Vector3 pD = new Vector3();
					
					boolean repeat = false;
					if(repeat){
						for(int i=0 ; i<wireframeVertcies.length ; i++){
							wireframeVertcies[i].z = MathUtils.random() * 200;
						}
					}
					
					for(int i=0 ; i<segments ; i++){
						float t = (float)i / (float)(segments-1);
						renderer.setColor(Color.ORANGE);
						renderer.line(v0, catmullrom.valueAt(v1, t));
						
						if(i % 2 == 0){
							
							catmullrom.derivativeAt(tangent, t).nor();

//							catmullrom.derivativeAt(binormal, (t + .1f) % 1f).nor();
//							binormal.crs(tangent).nor(); //.crs(tangent).nor();
//							normal.set(binormal).crs(tangent).nor();
							
//							catmullrom.derivativeAt(normal, (t + .1f) % 1f).nor();
//							binormal.set(Vector3.Z).crs(normal).nor();
//							
//							normal.set(Vector3.Z).crs(tangent).nor();
//							
//							binormal.crs(normal).nor();
//							normal.set(binormal).crs(tangent).nor();
							
							// normal.set(v1).add(0,0, 10).nor();
							
							normal.set(Vector3.Z).crs(tangent).nor(); // TODO not really Z
							binormal.set(tangent).crs(normal).nor();
							
							
							renderer.setColor(Color.GRAY);
//							int subseg = 4;
//							for(int j=0 ; j<subseg ; j++){
//								float qt = ((float)j / (float)(subseg)) * 2 - .5f;
//								float qv = ((float)j / (float)(subseg)) * 2 - .5f;
//								float qt2 = ((float)((j+1)%subseg) / (float)(subseg)) * 2 - .5f;
//								float qv2 = ((float)((j+1+subseg/2)%subseg) / (float)(subseg)) * 2 - .5f;
//								renderer.line(
//										new Vector3(v1).mulAdd(tangent, qt).mulAdd(binormal, qv), 
//										new Vector3(v1).mulAdd(tangent, qt2).mulAdd(binormal, qv2));
//							}
							
							Vector3 a = new Vector3(v1).mulAdd(normal, len * rw).mulAdd(binormal, len * rh);
							Vector3 b = new Vector3(v1).mulAdd(normal, -len * rw).mulAdd(binormal, len * rh);
							Vector3 c = new Vector3(v1).mulAdd(normal, -len * rw).mulAdd(binormal, -len * rh);
							Vector3 d = new Vector3(v1).mulAdd(normal, len * rw).mulAdd(binormal, -len * rh);
							
							renderer.line(a, b);
							renderer.line(b, c);
							renderer.line(c, d);
							renderer.line(d, a);
							
							if(i > 0){
								renderer.line(a, pA);
								renderer.line(b, pB);
								renderer.line(c, pC);
								renderer.line(d, pD);
								
							}
							
							pA.set(a);
							pB.set(b);
							pC.set(c);
							pD.set(d);
							
//							renderer.setColor(Color.RED);
//							renderer.line(v1, v0.set(v1).mulAdd(tangent, len));
//							renderer.setColor(Color.YELLOW);
//							renderer.line(v1, v0.set(v1).mulAdd(normal, len));
//							renderer.setColor(Color.GREEN);
//							renderer.line(v1, v0.set(v1).mulAdd(binormal, len));
						}
						v0.set(v1);
					}
					
					renderer.setColor(Color.YELLOW);
					for(CircuitCar car : circuit.cars){
						// catmullrom.valueAt(car.position, car.uvPos.x);
						float boxSize = 1;
						renderer.box(car.position.x - boxSize/2, car.position.y - boxSize/2, car.position.z - boxSize/2, boxSize, boxSize, boxSize);
					}
					
				}
				renderer.end();
			}else{
				renderer.begin(ShapeType.Point);
				renderer.point(wireframeVertcies[0].x, wireframeVertcies[0].y, wireframeVertcies[0].z);
				renderer.end();
			}
			
		}
	}

}

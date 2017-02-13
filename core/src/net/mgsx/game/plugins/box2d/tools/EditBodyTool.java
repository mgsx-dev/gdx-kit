package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.ComponentTool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.core.components.PolygonComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

// TODO generalize shape / box2D shape conversion
// TODO support all shapes ! ShapeAccessor ???
public class EditBodyTool extends ComponentTool
{
	private Entity current;
	private Array<Entity> shapeEntities;
	private Array<Entity> vertexEntities;

	public EditBodyTool(EditorScreen editor) {
		super("Edit body", editor, Family.all(Box2DBodyModel.class).get());
	}
	
	public static class ShapeComponent implements Component{
		
		public static ComponentMapper<EditBodyTool.ShapeComponent> components = ComponentMapper
				.getFor(EditBodyTool.ShapeComponent.class);
		public Box2DFixtureModel fixture;
	}
	
	@Override
	protected void activate() 
	{
		shapeEntities = new Array<Entity>();
		vertexEntities = new Array<Entity>();
		current = editor.currentEntity();
		
		// create entities for each shapes !
		Box2DBodyModel body = Box2DBodyModel.components.get(current);
		if(body == null) return;
		
		for(Box2DFixtureModel fixture : body.fixtures)
		{
			Entity fixtureEntity = getEngine().createEntity();
			ShapeComponent shapeComponent = editor.entityEngine.createComponent(ShapeComponent.class);
			fixtureEntity.add(shapeComponent);
			shapeComponent.fixture = fixture;
			PolygonComponent polygon = editor.entityEngine.createComponent(PolygonComponent.class);
			
			// TODO other : circle, loop, edge ...etc
			if(fixture.def.shape instanceof PolygonShape){
				polygon.loop = true;
				PolygonShape shape = ((PolygonShape)fixture.def.shape);
				for(int i=0 ; i<shape.getVertexCount() ; i++){
					Entity vertexEntity = getEngine().createEntity();
					Transform2DComponent transform = editor.entityEngine.createComponent(Transform2DComponent.class);
					shape.getVertex(i, transform.position);
					vertexEntity.add(transform);
					vertexEntities.add(vertexEntity);
					
					transform.position.add(body.body.getPosition());
					polygon.vertex.add(transform.position);
					
					getEngine().addEntity(vertexEntity);
				}
			}
			else if(fixture.def.shape instanceof ChainShape)
			{
				ChainShape shape =  ((ChainShape)fixture.def.shape);
				polygon.loop = shape.isLooped();
				int vcount = shape.getVertexCount() + (polygon.loop ? -1 : 0); // don't copy last vertex (same as first)
				for(int i=0 ; i<vcount ; i++){
					Entity vertexEntity = getEngine().createEntity();
					Transform2DComponent transform = editor.entityEngine.createComponent(Transform2DComponent.class);
					shape.getVertex(i, transform.position);
					vertexEntity.add(transform);
					vertexEntities.add(vertexEntity);
					
					transform.position.add(body.body.getPosition());
					polygon.vertex.add(transform.position);
					
					getEngine().addEntity(vertexEntity);
				}
			}
			
			fixtureEntity.add(polygon);
			shapeEntities.add(fixtureEntity);
			
			getEngine().addEntity(fixtureEntity);
		}
		
	}
	
	@Override
	protected void desactivate() 
	{
		Box2DBodyModel body = Box2DBodyModel.components.get(current);
		
		// restore all back (remove entities)
		for(Entity entity : shapeEntities)
		{
			ShapeComponent shape = ShapeComponent.components.get(entity);
			if(shape.fixture.fixture.getShape() instanceof PolygonShape){
				PolygonShape polygonShape = ((PolygonShape)shape.fixture.fixture.getShape());
				
				PolygonComponent polygon = PolygonComponent.components.get(entity);
				
				// reset body transform
				for(Vector2 v : polygon.vertex) v.sub(body.body.getPosition());

				// copy
				Vector2 [] vertex = new Vector2[polygon.vertex.size + (polygon.loop ? 1 : 0)];
				for(int i=0 ; i<vertex.length ; i++){
					vertex[i] = polygon.vertex.get(i % polygon.vertex.size);
				}
				polygonShape.set(vertex);
				PolygonShape polygonShapeDef = ((PolygonShape)shape.fixture.def.shape);
				polygonShapeDef.set(vertex);
			}
			else if(shape.fixture.fixture.getShape() instanceof ChainShape){
				
				// TODO require to recreate shape fully and then fixture. Then
				// any final reference to fixture or shape will be broken.
				
				PolygonComponent polygon = PolygonComponent.components.get(entity);
				
				// reset body transform
				for(Vector2 v : polygon.vertex) v.sub(body.body.getPosition());

				// copy
				Vector2 [] vertex = new Vector2[polygon.vertex.size];
				for(int i=0 ; i<vertex.length ; i++){
					vertex[i] = polygon.vertex.get(i);
				}
				
				ChainShape polygonShape = new ChainShape();
				if(polygon.loop)
					polygonShape.createLoop(vertex);
				else
					polygonShape.createChain(vertex);
				
				// relink all together (listeners ... body / fixture)
				shape.fixture.def.shape = polygonShape;
				
				Fixture oldFixture = shape.fixture.fixture;
				
				body.body.destroyFixture(oldFixture);
				Fixture newFixture = body.body.createFixture(shape.fixture.def);
				newFixture.setUserData(oldFixture.getUserData());
				shape.fixture.fixture = newFixture;
				
			}
			// TODO other shapes again
			
			editor.entityEngine.removeEntity(entity);
		}
		for(Entity entity : vertexEntities)
		{
			editor.entityEngine.removeEntity(entity);
		}
		
		editor.selection.clear();
		editor.selection.add(current);
		editor.invalidateSelection();
		
		shapeEntities.clear();
		vertexEntities.clear();
		current = null;
		
		super.desactivate();
	}

	@Override
	protected Component createComponent(Entity entity) {
		// we don't create component here : just some other entities.
		return null;
	}

}

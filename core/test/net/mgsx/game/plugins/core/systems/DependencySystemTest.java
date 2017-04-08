package net.mgsx.game.plugins.core.systems;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.plugins.core.systems.DependencySystem.LinkListener;

public class DependencySystemTest {

	private static float deltaTime = 1;
	
	private int unlinkCount;
	
	@Before
	public void setup(){
		unlinkCount = 0;
	}
	
	@Test
	public void testCustomLink()
	{
		DependencySystem system = new DependencySystem();
		Engine engine = new Engine();
		engine.addSystem(system);
		
		final Entity parent = engine.createEntity();
		engine.addEntity(parent);
		
		final Entity child = engine.createEntity();
		engine.addEntity(child);
		
		LinkListener listener = new LinkListener() {
			
			@Override
			public void onUnlink(Entity cParent, Entity cChild) {
				if(parent == cParent && child == cChild)
					unlinkCount++;
				else
					Assert.fail();
			}
		};
		
		system.link(parent, child, listener);
		
		engine.removeEntity(child);
		
		Assert.assertEquals(1, unlinkCount);
	}
	
	@Test
	public void testParentChild()
	{
		DependencySystem system = new DependencySystem();
		Engine engine = new Engine();
		engine.addSystem(system);
		
		final Entity dummy = engine.createEntity();
		engine.addEntity(dummy);

		final Entity parent = engine.createEntity();
		engine.addEntity(parent);
		
		final Entity child = engine.createEntity();
		engine.addEntity(child);
		
		system.link(parent, child);
		
		engine.removeEntity(parent);
		
		engine.update(deltaTime);;
		
		Assert.assertEquals(1, engine.getEntities().size());
		Assert.assertSame(dummy, engine.getEntities().first());
	}

	@Test
	public void testParentChildRemove()
	{
		DependencySystem system = new DependencySystem();
		Engine engine = new Engine();
		engine.addSystem(system);
		
		final Entity dummy = engine.createEntity();
		engine.addEntity(dummy);

		final Entity parent = engine.createEntity();
		engine.addEntity(parent);
		
		for(int i=0 ; i<20 ; i++){
			final Entity child = engine.createEntity();
			engine.addEntity(child);
			system.link(parent, child);
		}
		
		
		system.removeChildren(parent);
		
		engine.update(deltaTime);;
		
		Assert.assertEquals(2, engine.getEntities().size());
	}
	@Test
	public void testParentChildUnlink()
	{
		DependencySystem system = new DependencySystem();
		Engine engine = new Engine();
		engine.addSystem(system);
		
		final Entity dummy = engine.createEntity();
		engine.addEntity(dummy);

		final Entity parent = engine.createEntity();
		engine.addEntity(parent);
		
		for(int i=0 ; i<20 ; i++){
			final Entity child = engine.createEntity();
			engine.addEntity(child);
			system.link(parent, child);
		}
		
		Array<Entity> children = system.unlinkChildren(parent);
		Assert.assertEquals(20, children.size);
		
		engine.removeEntity(parent);
		
		engine.update(deltaTime);
		
		Assert.assertEquals(21, engine.getEntities().size());
	}

}

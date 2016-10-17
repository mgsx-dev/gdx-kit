package net.mgsx;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;

public class AshleyTests {

	public static class MyComponent implements Component{}
	public static class MyComponent2 implements Component{}
	
	public static void main(String[] args) {
		
		PooledEngine engine = new PooledEngine();
		
		
		engine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(final Entity entity) {
				String str = entity.toString();
				for(Component c : entity.getComponents()){
					str += ", " + c.toString();
				}
				System.out.println("entity removed : " + str);
			}
			@Override
			public void entityAdded(final Entity entity) {
				String str = entity.toString();
				for(Component c : entity.getComponents()){
					str += ", " + c.toString();
				}
				System.out.println("entity added : " + str);
			}
		});
		
		engine.addEntityListener(Family.one(MyComponent.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(final Entity entity) {
				String str = entity.toString();
				for(Component c : entity.getComponents()){
					str += ", " + c.toString();
				}
				System.out.println("MyComponent removed : " + str);
			}
			@Override
			public void entityAdded(final Entity entity) {
				String str = entity.toString();
				for(Component c : entity.getComponents()){
					str += ", " + c.toString();
				}
				System.out.println("MyComponent added : " + str);
			}
		});
		
		Entity e = engine.createEntity();
		
		e.add(new MyComponent());
		
		e.remove(MyComponent.class);
		
		e.add(new MyComponent());

		engine.addEntity(e);
		
		e.remove(MyComponent.class);
		
		e.add(new MyComponent());

		e.add(new MyComponent2());
		e.remove(MyComponent2.class);

		engine.removeEntity(e);
		
		e.remove(MyComponent.class);
		
		e.add(new MyComponent());
	}
}

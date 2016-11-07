package net.mgsx.game.plugins.fsm.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.fsm.components.StateComponent;

public abstract class EntityStateSystem extends IteratingSystem implements EntityListener
{
	private Family stateFamily;
	
	private Class<? extends Component> stateComponentType;

	/**
	 * System initialization
	 * @param stateComponentType state component to listen to
	 * @param builder family builder : call {@link #configure()} and add all/one/exclude query (order isn't important)
	 */
	public EntityStateSystem(Class<? extends StateComponent> stateComponentType, Builder builder) {
		super(builder.all(stateComponentType).get(), GamePipeline.LOGIC);
		this.stateComponentType = stateComponentType;
		stateFamily = Family.all(stateComponentType).get();
	}
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		engine.addEntityListener(stateFamily, this);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}
	
	
	public static class Builder
	{
		private Array<Class<? extends Component>> all = new Array<Class<? extends Component>>();
		private Array<Class<? extends Component>> one = new Array<Class<? extends Component>>();
		private Array<Class<? extends Component>> exclude = new Array<Class<? extends Component>>();
		public Builder all(Class<? extends Component>... componentTypes){
			all.addAll(componentTypes);
			return this;
		}
		public Builder one(Class<? extends Component>... componentTypes){
			one.addAll(componentTypes);
			return this;
		}
		public Builder exclude(Class<? extends Component>... componentTypes){
			exclude.addAll(componentTypes);
			return this;
		}
		public Builder reset(){
			all.clear();
			one.clear();
			exclude.clear();
			return this;
		}
		public Family get(){
			return Family.all(all.shrink()).one(one.shrink()).exclude(exclude.shrink()).get();
		}
	}
	
	private final static Builder builder = new Builder();
	
	public static Builder configure(){
		return builder.reset();
	}
	
	
	@Override
	public void entityAdded(Entity entity) {
		enter(entity);
	}

	@Override
	public void entityRemoved(Entity entity) {
		exit(entity);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		update(entity, deltaTime);
	}
	
	protected void change(Entity entity, Class<? extends StateComponent> newState)
	{
		entity.remove(stateComponentType);
		entity.add(getEngine().createComponent(newState));
	}
	
	abstract protected void enter(Entity entity);
	abstract protected void update(Entity entity, float deltaTime);
	abstract protected void exit(Entity entity);
}

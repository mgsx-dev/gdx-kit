package net.mgsx.game.plugins.fsm.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.fsm.components.StateComponent;

public abstract class EntityStateSystem<T extends StateComponent> extends IteratingSystem implements EntityListener
{
	private Family stateFamily;
	
	private Class<T> stateComponentType;
	
	private ComponentMapper<T> mapper;

	/**
	 * System initialization
	 * @param stateComponentType state component to listen to
	 * @param builder family builder : call {@link #configure()} and add all/one/exclude query (order isn't important)
	 */
	public EntityStateSystem(Class<T> stateComponentType, Builder builder) {
		super(builder.all(stateComponentType).get(), GamePipeline.LOGIC);
		this.stateComponentType = stateComponentType;
		stateFamily = Family.all(stateComponentType).get();
		mapper = ComponentMapper.getFor(stateComponentType);
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
	
	protected T state(Entity entity){
		return mapper.get(entity);
	}
	
	
	public static class Builder
	{
		private Array<Class<? extends Component>> all = new Array<Class<? extends Component>>(Class.class);
		private Array<Class<? extends Component>> one = new Array<Class<? extends Component>>(Class.class);
		private Array<Class<? extends Component>> exclude = new Array<Class<? extends Component>>(Class.class);
		
		private Builder() {
			super();
		}
		public Builder all(Class<? extends Component>... componentTypes){
			for(Class<? extends Component> componentType : componentTypes) all.add(componentType);
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
			return Family.all(all.toArray()).one(one.toArray()).exclude(exclude.toArray()).get();
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
		// nothing to do since component is already recycled.
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		update(entity, deltaTime);
	}
	
	protected void change(Entity entity, Class<? extends StateComponent> newState)
	{
		exit(entity); // call exit before remove
		entity.remove(stateComponentType);
		entity.add(getEngine().createComponent(newState));
	}
	
	abstract protected void enter(Entity entity);
	abstract protected void update(Entity entity, float deltaTime);
	abstract protected void exit(Entity entity);
}

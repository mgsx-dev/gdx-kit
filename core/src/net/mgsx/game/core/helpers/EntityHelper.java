package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.plugins.Initializable;

/**
 * Helper for Ashley extension 
 */
public class EntityHelper 
{
	public static Entity first(Engine engine, Family family){
		ImmutableArray<Entity> elements = engine.getEntitiesFor(family);
		return elements.size() > 0 ? elements.first() : null;
	}

	public static Entity clone(Engine engine, Entity entityTemplate) 
	{
		Entity entityClone = engine.createEntity();
		
		// clone duplicable components
		for(Component componentTemplate : entityTemplate.getComponents()){
			if(componentTemplate instanceof Duplicable){
				Component componentClone = ((Duplicable) componentTemplate).duplicate(engine);
				entityClone.add(componentClone);
			}
			else{
				EditableComponent editable = componentTemplate.getClass().getAnnotation(EditableComponent.class);
				if(editable != null && editable.autoClone()){
					Component componentClone = engine.createComponent(componentTemplate.getClass());
					ReflectionHelper.copy(componentClone, componentTemplate);
					entityClone.add(componentClone);
				}
			}
		}
		
		// initialize components
		for(Component componentClone : entityClone.getComponents()){
			if(componentClone instanceof Initializable){
				((Initializable) componentClone).initialize(engine, entityClone);
			}
		}
		
		engine.addEntity(entityClone);
		
		return entityClone;
	}

	public static <T extends Component> T getOrCreate(Engine engine, Entity entity, Class<T> type) {
		T component = entity.getComponent(type);
		if(component == null){
			component = engine.createComponent(type);
			entity.add(component);
		}
		return component;
	}
	
}

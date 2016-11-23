package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.ContextualDuplicable;
import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.components.Initializable;
import net.mgsx.game.core.storage.EntityGroup;

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
		return clone(engine, entityTemplate, null, null, true);
	}
	public static Entity clone(Engine engine, Entity entityTemplate, EntityGroup templateContext, EntityGroup cloneContext, boolean addToEngine) 
	{
		Entity entityClone = engine.createEntity();
		if(cloneContext != null) cloneContext.add(entityClone);
		
		// clone duplicable components
		for(Component componentTemplate : entityTemplate.getComponents())
		{
			Component componentClone = null;
			
			// first priority : custom duplication
			if(componentClone == null && componentTemplate instanceof ContextualDuplicable){
				if(templateContext == null || cloneContext == null){
					Gdx.app.error("core", "component " + componentTemplate.getClass().getName() + " not copied : require context for duplication.");
					continue;
				}else{
					componentClone = ((ContextualDuplicable) componentTemplate).duplicate(engine, templateContext, cloneContext);
				}
			}
			if(componentClone == null && componentTemplate instanceof Duplicable){
				componentClone = ((Duplicable) componentTemplate).duplicate(engine);
			}
			// second priority : tagged as autoClone
			EditableComponent editable = componentTemplate.getClass().getAnnotation(EditableComponent.class);
			if(componentClone == null && editable != null && editable.autoClone()){
				componentClone = engine.createComponent(componentTemplate.getClass());
				ReflectionHelper.copy(componentClone, componentTemplate);
			}
			// last priority : tagged as Storable, just create a new component without copy anything.
			Storable storable = componentTemplate.getClass().getAnnotation(Storable.class);
			if(componentClone == null && storable != null){
				componentClone = engine.createComponent(componentTemplate.getClass());
			}
			
			// finally add component clone to entity clone.
			if(componentClone != null){
				entityClone.add(componentClone);
			}
		}
		
		if(addToEngine){
			// add new entity to engine before post initialization.
			engine.addEntity(entityClone);
			
			// initialize components
			for(Component componentClone : entityClone.getComponents()){
				if(componentClone instanceof Initializable){
					((Initializable) componentClone).initialize(engine, entityClone);
				}
			}
		}
		
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

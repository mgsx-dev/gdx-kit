package com.badlogic.gdx.ai.btree;

import java.lang.reflect.Field;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.helpers.ReflectionHelper;

/**
 * Generic task cloner based on {@link TaskAttribute} annotation.
 * {@link Task#copyTo(Task)} method is called after to allow copy overrides.
 * It using {@link Pool} to reduce allocations.
 * 
 * Any behavior tree obtain by {@link BehaviorTreeLibrary} should give it back to pools
 * by calling {@link #free(Task)}.
 * 
 * XXX package com.badlogic.gdx.ai.btree is required to access {@link Task#copyTo(Task)} method.
 * 
 * @author mgsx
 *
 */
public class AnnotationBasedTaskCloner implements TaskCloner {

	// TODO refactor as type pools ?
	private static final ObjectMap<Class, Pool<Task>> pools = new ObjectMap<Class, Pool<Task>>();
	
	private static <T> Task<T> obtain(final Class type){
		Pool<Task> pool = pools.get(type);
		if(pool == null) pools.put(type, new Pool<Task>(){
			@Override
			protected Task newObject() {
				return ReflectionHelper.newInstance(type);
			}
		});
		return pool.obtain();
	}
	
	public static void free(Task task) {
		Pool<Task> pool = pools.get(task.getClass());
		if(pool != null){
			pool.free(task);
		}
		for(int i=0 ; i<task.getChildCount() ; i++){
			free(task.getChild(i));
		}
	}
	
	@Override
	public <T> Task<T> cloneTask(Task<T> task) 
	{
		Task<T> clone = obtain(task.getClass());
		for(Field field : task.getClass().getFields()){
			TaskAttribute attr = field.getAnnotation(TaskAttribute.class);
			if(attr != null){
				ReflectionHelper.set(clone, field, ReflectionHelper.get(task, field));
			}
		}
		return task.copyTo(clone);
	}

}

package com.badlogic.gdx.ai.btree;

import java.lang.reflect.Field;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.core.helpers.ReflectionHelper;

public class AnnotationBasedTaskCloner implements TaskCloner {

	@Override
	public <T> Task<T> cloneTask(Task<T> task) 
	{
		// TODO use pool by type ?
		Task<T> clone = ReflectionHelper.newInstance(task.getClass());
		task.copyTo(clone);
		for(Field field : task.getClass().getFields()){
			TaskAttribute attr = field.getAnnotation(TaskAttribute.class);
			if(attr != null){
				ReflectionHelper.set(clone, field, ReflectionHelper.get(task, field));
			}
		}
		return clone;
	}

}

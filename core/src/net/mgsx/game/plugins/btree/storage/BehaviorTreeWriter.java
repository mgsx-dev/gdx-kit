package net.mgsx.game.plugins.btree.storage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

public class BehaviorTreeWriter {

	private ObjectSet<Class> types = new ObjectSet<Class>();
	private ObjectMap<Class, String> typeNames = new ObjectMap<Class, String>();
	private ObjectSet<Class> aliases = new ObjectSet<Class>();
	
	private void scan(Task task){
		types.add(task.getClass());
		for(int i=0 ; i<task.getChildCount() ; i++)
			scan(task.getChild(i));
	}
	
	public void write(BehaviorTree tree, FileHandle file){
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(file.file());
		} catch (FileNotFoundException e) {
			throw new GdxRuntimeException(e);
		}
		
		// first scan all types in tree
		scan(tree);
		
		// map some names (gdx-ai builtin types and annotated with @TaskAlias)
		for(Class type : types){
			if(type.getPackage().getName().startsWith("com.badlogic.gdx.ai.btree")){
				typeNames.put(type, type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1, type.getSimpleName().length()));
				continue;
			}
			TaskAlias alias = (TaskAlias)type.getAnnotation(TaskAlias.class);
			if(alias != null){
				aliases.add(type);
				typeNames.put(type, alias.value());
				continue;
			}
			typeNames.put(type, type.getName());
		}
		
		// write imports (for annotated types)
		for(Class type : aliases){
			writer.println("import " + typeNames.get(type) + ":" + "\"" + type.getName() + "\"");
		}
		writer.println();
		
		
		// write tree using mapped names
		// write fields annotated with @TaskAttribute
		writeTree(writer, tree.getChild(0), 0);
		
		
		writer.close();
		
		// note that distribution and sub tree reference can't be preserved.
	}

	private void writeTree(PrintWriter writer, Task task, int level) 
	{
		for(int i=0 ; i<level ; i++) writer.print("  ");
		writer.print(typeNames.get(task.getClass()));
		
		Task def = ReflectionHelper.newInstance(task.getClass());
		
		for(Field field : task.getClass().getFields()){
			TaskAttribute attribute = field.getAnnotation(TaskAttribute.class);
			if(attribute != null){
				Object value = ReflectionHelper.get(task, field);
				Object defValue = ReflectionHelper.get(def, field);
				if(defValue == null && value == null || defValue != null && defValue.equals(value)){
					continue;
				}
				String name = attribute.name().isEmpty() ? field.getName() : attribute.name();
				String fmtValue;
				if(value instanceof String || field.getType().isEnum()){
					fmtValue = "\"" + value + "\"";
				}else if(value instanceof ConstantFloatDistribution){
					ConstantFloatDistribution distrib = (ConstantFloatDistribution)value;
					fmtValue = String.valueOf(distrib.getValue());
					// TODO other ... need to check every types ...
				}else{
					fmtValue = value.toString();
				}
				writer.print(" " + name + ":" + fmtValue);
			}
		}
		writer.println();
		
		for(int i=0 ; i<task.getChildCount() ; i++){
			writeTree(writer, task.getChild(i), level+1);
		}
	}
}

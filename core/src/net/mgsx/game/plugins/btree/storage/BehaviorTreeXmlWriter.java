package net.mgsx.game.plugins.btree.storage;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.XmlWriter;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

/**
 * Write behavior tree to file using XML format.
 * 
 * @author mgsx
 *
 */
public class BehaviorTreeXmlWriter {

	private ObjectSet<Class> types = new ObjectSet<Class>();
	private ObjectMap<Class, String> typeNames = new ObjectMap<Class, String>();
	private ObjectSet<Class> aliases = new ObjectSet<Class>();
	
	private void scan(Task task){
		types.add(task.getClass());
		for(int i=0 ; i<task.getChildCount() ; i++)
			scan(task.getChild(i));
	}
	
	public void write(BehaviorTree tree, FileHandle file) {
		try {
			write(tree, Gdx.files.absolute(file.file().getAbsolutePath()).writer(false));
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	private void write(BehaviorTree tree, Writer w) throws IOException{
		
		XmlWriter writer = new XmlWriter(w);
		
		// first scan all types in tree
		scan(tree);
		
		types.addAll(ClassRegistry.instance.getSubTypesOf(Task.class));
		
		// map some names (gdx-ai builtin types and annotated with @TaskAlias)
		for(Class type : types){
			if(Modifier.isAbstract(type.getModifiers())) continue;
			
			if(type.getPackage().getName().startsWith("com.badlogic.gdx.ai.btree")){
				String alias = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1, type.getSimpleName().length());
				typeNames.put(type, alias);
				aliases.add(type);
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
		
		Array<Class> sortedClasses = ArrayHelper.array(aliases);
		sortedClasses.sort(new Comparator<Class>() {
			@Override
			public int compare(Class o1, Class o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		writer.element("BehaviorTree");
		
		writer.element("imports");
		
		// write imports (for annotated types)
		for(Class type : sortedClasses){
			writer.element("import");
			writer.attribute("alias", typeNames.get(type));
			writer.attribute("type", type.getName());
			writer.pop();
		}
		writer.pop();
		
		
		// write tree using mapped names
		// write fields annotated with @TaskAttribute
		writer.element("root");
		writeTree(writer, tree.getChild(0), 0);
		writer.pop();
		
		writer.close();
		
		// note that distribution and sub tree reference can't be preserved.
	}

	private void writeTree(XmlWriter writer, Task task, int level) throws IOException 
	{
		writer.element(typeNames.get(task.getClass()));
		
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
				String fmtValue = null;
				if(value instanceof ConstantFloatDistribution){
					ConstantFloatDistribution distrib = (ConstantFloatDistribution)value;
					fmtValue = String.valueOf(distrib.getValue());
					// TODO other ... need to check every types ...
				}else if(value instanceof Interpolation){
					for(Field f : Interpolation.class.getFields()){
						if(Modifier.isStatic(f.getModifiers()) && Interpolation.class.isAssignableFrom(f.getType())){
							if(value == ReflectionHelper.get(null, f)){
								fmtValue = f.getName();
							}
						}
					}
				}
				if(fmtValue == null){
					fmtValue = value.toString();
				}
				writer.attribute(name, fmtValue);
			}
		}
		
		for(int i=0 ; i<task.getChildCount() ; i++){
			writeTree(writer, task.getChild(i), level+1);
		}
		writer.pop();
	}
}

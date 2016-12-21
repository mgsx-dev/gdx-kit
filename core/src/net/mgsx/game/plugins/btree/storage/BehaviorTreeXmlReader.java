package net.mgsx.game.plugins.btree.storage;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.ai.utils.random.Distribution;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import net.mgsx.game.core.helpers.ReflectionHelper;

public class BehaviorTreeXmlReader
{
	private ObjectMap<String, String> aliases = new ObjectMap<String, String>();
	
	public void read(BehaviorTree tree, Reader reader){
		try {
			Element root = new XmlReader().parse(reader);
			
			Element imports = root.getChildByName("imports");
			for(int i=0 ; i<imports.getChildCount() ; i++){
				Element imp = imports.getChild(i);
				aliases.put(imp.get("alias"), imp.get("type"));
			}
			
			
			tree.addChild(read(root.getChildByName("root").getChild(0)));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Task read(Element element) 
	{
		String alias = element.getName();
		String type = aliases.get(alias);
		Task task = (Task)ReflectionHelper.newInstance(type);
		
		if(element.getAttributes() != null)
		for(Entry<String, String> entry : element.getAttributes().entries()){
			String name = entry.key;
			String value = entry.value;
			
			Field field = ReflectionHelper.field(task.getClass(), name);
			Class attributeType = field.getType();
			
			if(attributeType == String.class){
				ReflectionHelper.set(task, field, value);
			}
			else if(attributeType == float.class){
				ReflectionHelper.set(task, field, Float.parseFloat(value));
			}
			else if(attributeType == int.class){
				ReflectionHelper.set(task, field, Integer.parseInt(value));
			}
			else if(attributeType == boolean.class){
				ReflectionHelper.set(task, field, Boolean.parseBoolean(value));
			}
			else if(Distribution.class.isAssignableFrom(attributeType)){
				if(ConstantFloatDistribution.class.isAssignableFrom(attributeType)){
					ReflectionHelper.set(task, field, new ConstantFloatDistribution(Float.parseFloat(value)));
				}else{
					Gdx.app.error("BTree XML", "unsupported distribution " + attributeType);
				}
			}
			else if(attributeType == Interpolation.class){
				Field f = ReflectionHelper.field(Interpolation.class, value);
				if(f != null) ReflectionHelper.set(task, field, ReflectionHelper.get(null, f));
				else Gdx.app.error("BTree XML", "unsupported interpolation " + value);
			}
			else {
				Gdx.app.error("BTree XML", "unsupported type " + attributeType);
			}
		}
		
		for(int i=0 ; i<element.getChildCount() ; i++){
			Element child = element.getChild(i);
			task.addChild(read(child));
		}
		
		return task;
	}
}

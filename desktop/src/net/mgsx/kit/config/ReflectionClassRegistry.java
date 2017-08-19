package net.mgsx.kit.config;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.badlogic.gdx.utils.Array;
import com.google.common.base.Predicate;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.meta.ClassRegistry;

public class ReflectionClassRegistry extends ClassRegistry
{
	/** required in order to scan EntityLeafTask */
	public static final String behaviorTree = "com.badlogic.gdx.ai.btree";
	
	private Reflections reflections;
	
	public ReflectionClassRegistry(final String ...packages) 
	{
		Collection<URL> urls = new ArrayList<URL>();
		for(String url : packages){
			urls.addAll(ClasspathHelper.getUrlsForPackagePrefix(url));
		}
		reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new Predicate<String>() {
					@Override
					public boolean apply(String input) {
						// executed in separate thread
						boolean match = false;
						for(String name : packages){
							if(input.startsWith(name)){
								match = true;
								break;
							}
						}
						return match;
					}
				})
			     .setUrls(urls)
			     .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
	}
	
	
	@Override
	public Array<Class> getSubTypesOf(Class type) {
		return ArrayHelper.array(reflections.getSubTypesOf(type));
	}

	@Override
	public Array<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
		return ArrayHelper.array(reflections.getTypesAnnotatedWith(annotation));
	}


	@Override
	public Array<Class<?>> getClasses() {
		Array<Class<?>> r = new Array<Class<?>>();
		for(Entry<String, String> entry : reflections.getStore().get(TypeAnnotationsScanner.class).entries()){
			r.add(ReflectionHelper.forName(entry.getValue()));
		}
		return r;
	}

}

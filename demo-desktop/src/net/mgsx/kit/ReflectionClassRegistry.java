package net.mgsx.kit;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.meta.ClassRegistry;

public class ReflectionClassRegistry extends ClassRegistry
{
	private Reflections reflections;
	
	public ReflectionClassRegistry() 
	{
		Collection<URL> urls = ClasspathHelper.getUrlsForPackagePrefix("net.mgsx.game");
		urls.addAll(ClasspathHelper.getUrlsForPackagePrefix("com.badlogic.gdx.ai.btree"));
		reflections = new Reflections(
				new ConfigurationBuilder()
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

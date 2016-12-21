package net.mgsx.game.core.meta;

import java.io.PrintWriter;
import java.util.Comparator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Kit;

public class ClassRegistryExporter {

	public void export(ClassRegistry registry, FileHandle folder){
		
		String typeName = "KitClass";
		FileHandle file = folder.child("net/mgsx/kit/" + typeName + ".java");
		
		PrintWriter w = new PrintWriter(file.write(false));
		
		w.println("package net.mgsx.kit;");
		w.println();
		w.print("@" + Kit.class.getName() + "(dependencies={");
		
		int i=0;
		Array<Class<?>> classes = registry.getClasses();
		classes.sort(new Comparator<Class<?>>() {

			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for(Class<?> cls : classes){
			w.print((i>0 ? ",\n\t": "") + cls.getName().replaceAll("\\$", ".") + ".class");
			i++;
		}
		
		w.println("})");
		
		w.println("public class " + typeName + " {}");
		
		w.close();
		
	}
}

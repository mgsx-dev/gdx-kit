package net.mgsx.game.core.helpers;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHelper {

	public static FileHandle classpath(Class cls, String path)
	{
		return Gdx.files.classpath(cls.getPackage().getName().replaceAll("\\.", "/") + "/" + path); // TODO !!!
	}
	
	/**
	 * Convert a path to a relative to current working directory path.
	 * (Desktop only)
	 * @param fileName a path (absolute or internal)
	 * @return the converted (strip) path or null if can't strip.
	 */
	public static String stripPath(String fileName) {
		
		FileHandle file = Gdx.files.internal(fileName);
		if(file.exists()){
			String base = new File("").getAbsolutePath();
			if(fileName.startsWith(base)){
				return fileName.substring(base.length() + 1);
			}
		}
		return null;
	}
}

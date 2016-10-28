package net.mgsx.game.core.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHelper {

	public static FileHandle classpath(Class cls, String path)
	{
		return Gdx.files.classpath(cls.getPackage().getName().replaceAll("\\.", "/") + "/" + path); // TODO !!!
	}
}

package net.mgsx.game.core.storage;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameRegistry;

public class SaveConfiguration {
	public AssetManager assets;
	public Engine engine;
	public GameRegistry registry;
	public boolean pretty;
	public boolean stripPaths;
	
	public Array<Message> messages = new Array<Message>();
	
	public static class Message{
		public String tag = "";
		public String description = "";
		public String fullDescription = null;
	}
	
	public void error(String message, Throwable e){
		StringWriter w = new StringWriter();
		e.printStackTrace(new PrintWriter(w));
		
		Message m = new Message();
		m.tag = "Save";
		m.description = message;
		m.fullDescription = w.toString();
		messages.add(m);
	}

	public void warn(String message) {
		Message m = new Message();
		m.tag = "Save";
		m.description = message;
		messages.add(m);
	}
}

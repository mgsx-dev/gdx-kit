package net.mgsx.game.core.tools;

import net.mgsx.game.core.EditorScreen;

/**
 * Tool activated by pressing a single keyboard key.
 * 
 * @author mgsx
 *
 */
abstract public class KeyTool extends Tool
{
	private int key;

	public KeyTool(String name, EditorScreen editor, int key) {
		super(name, editor);
		this.key = key;
	}
	public KeyTool(EditorScreen editor, int key) {
		super(editor);
		this.key = key;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == key){
			run();
		}
		// TODO Auto-generated method stub
		return super.keyDown(keycode);
	}

	abstract protected void run();

}

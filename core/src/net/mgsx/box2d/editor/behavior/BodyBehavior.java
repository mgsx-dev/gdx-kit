package net.mgsx.box2d.editor.behavior;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.Behavior;

abstract public class BodyBehavior implements Behavior
{
	public WorldItem worldItem;
	public BodyItem bodyItem;
	
	public void renderDebug(ShapeRenderer renderer){
		
	}

}

package net.mgsx.plugins.box2d.behavior;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.mgsx.core.Behavior;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

abstract public class BodyBehavior implements Behavior
{
	public WorldItem worldItem;
	public BodyItem bodyItem;
	
	public void renderDebug(ShapeRenderer renderer){}

}

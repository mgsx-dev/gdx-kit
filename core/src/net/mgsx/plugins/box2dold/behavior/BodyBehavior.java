package net.mgsx.plugins.box2dold.behavior;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.mgsx.core.Behavior;
import net.mgsx.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.plugins.box2dold.model.WorldItem;

abstract public class BodyBehavior implements Behavior
{
	public WorldItem worldItem;
	public Box2DBodyModel bodyItem;
	
	public void renderDebug(ShapeRenderer renderer){}

}

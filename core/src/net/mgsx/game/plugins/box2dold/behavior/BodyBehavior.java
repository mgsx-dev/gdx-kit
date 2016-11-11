package net.mgsx.game.plugins.box2dold.behavior;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.mgsx.game.core.helpers.Behavior;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;

abstract public class BodyBehavior implements Behavior
{
	public WorldItem worldItem;
	public Box2DBodyModel bodyItem;
	
	public void renderDebug(ShapeRenderer renderer){}

}

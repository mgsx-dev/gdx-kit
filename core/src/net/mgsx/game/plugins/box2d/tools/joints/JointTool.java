package net.mgsx.game.plugins.box2d.tools.joints;

import com.badlogic.gdx.physics.box2d.JointDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2dold.commands.Box2DCommands;

abstract public class JointTool<T extends JointDef> extends MultiClickTool 
{
	protected WorldItem worldItem;
	
	public JointTool(String name, EditorScreen editor, WorldItem worldItem, int maxPoints) 
	{
		super("Create " + name, editor, maxPoints);
		this.worldItem = worldItem;
	}
	
	protected abstract T createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB);
	
	@Override
	protected void complete() 
	{
		// TODO based on editor !
		if(editor.selection.size < 2){
			return;
		}
		Box2DBodyModel bodyA = editor.selection.get(editor.selection.size-2).getComponent(Box2DBodyModel.class);
		Box2DBodyModel bodyB = editor.selection.get(editor.selection.size-1).getComponent(Box2DBodyModel.class);
		
		final T def = createJoint(bodyA, bodyB);
		
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		
		editor.performCommand(Box2DCommands.addJoint(editor, worldItem.world, name, def));
		
		end();
	}
}

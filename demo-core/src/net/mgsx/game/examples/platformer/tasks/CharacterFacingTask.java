package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.animations.Character2D;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("characterFacing")
public class CharacterFacingTask extends EntityLeafTask
{
	@Override
	public void start() {
		Character2D character = Character2D.components.get(getEntity());
		if(character != null){
			character.facing = true;
		}
	}
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		return Status.RUNNING;
	}
	@Override
	public void end() {
		Character2D character = Character2D.components.get(getEntity());
		if(character != null){
			character.facing = false;
		}
	}
	
}

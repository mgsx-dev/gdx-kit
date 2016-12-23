package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.animations.Character2D;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("onWall")
public class OnWallCondition extends ConditionTask
{

	@Override
	public boolean match() {
		PlayerController player = PlayerController.components.get(getEntity());
		if(player == null) return false;
		Character2D character = Character2D.components.get(getEntity());
		if(character == null) return false;
		return !player.onGround && ((player.onWallLeft && character.rightToLeft) ||
				(player.onWallRight && !character.rightToLeft));
	}

}

package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.examples.platformer.sensors.SugarZone;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("onSugar")
public class OnSugar extends ConditionTask
{
	@Override
	public boolean match() {
		PlayerController player = PlayerController.components.get(getEntity());
		if(player != null && player.lastGroundFixture != null){
			
			Entity e = (Entity)player.lastGroundFixture.getBody().getUserData();
			if(e != null && SugarZone.components.has(e)){
				return true;
			}
		}
		return false;
	}

}

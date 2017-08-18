package net.mgsx.game.examples.td.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public class BuyCanonTowerAction extends AbstractTileEditAction
{
	public BuyCanonTowerAction(Engine engine) {
		super(engine);
	}

	@Override
	public void apply(Entity cell) 
	{
		Entity tower = getEngine().createEntity();
		
		// TODO load from template !
		
		getEngine().addEntity(tower);
	}

	@Override
	public boolean allowed() {
		return true; // TODO refactor to buy tower action : verify if cell is not full ... already have a tower
	}

}

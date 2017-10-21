package net.mgsx.game.examples.convoy.model;

import net.mgsx.game.examples.convoy.components.Planet;

public class Rules 
{

	public static Operation addGaz(final Universe universe, final Planet planet)
	{
		return new Operation() {
			
			@Override
			public void commit() {
				planet.gazAvailability++;
				universe.playerMoney -= price();
			}
			
			@Override
			public boolean allowed() {
				return planet.gazAvailability < 2 && universe.playerMoney >= price();
			}

			private float price() {
				return planet.gazAvailability * planet.gazAvailability * 1000 + 500;
			}
			
			@Override
			public String toString() {
				return planet.gazAvailability >= 2 ? "Gaz at MAX" : "Gaz extension for "+price()+"$";
			}
		};
	}
	
	// TODO add ship oil MAX
	// TODO add ship storage
	// TODO add ship speed
	// TODO add ....
	
	// TODO make a EVO graph ?
	// TODO evo curve (price/improvement...)
	
	
}

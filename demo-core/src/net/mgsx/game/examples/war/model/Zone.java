package net.mgsx.game.examples.war.model;

import com.badlogic.gdx.utils.ObjectMap;

public class Zone {

	public static class ZoneGoods
	{
		public int price;
		public int quantity;
		
		public History priceHistory = new History(10); // XXX max config
	}
	
	public ObjectMap<Goods, ZoneGoods> goods = new ObjectMap<Goods, Zone.ZoneGoods>();
}

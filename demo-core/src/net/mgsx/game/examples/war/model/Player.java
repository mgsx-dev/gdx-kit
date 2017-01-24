package net.mgsx.game.examples.war.model;

import com.badlogic.gdx.utils.ObjectMap;

public class Player {
	public static class PlayerGoods{
		public int quantity;
		public float value;
	}
	public ObjectMap<Goods, PlayerGoods> goods;
	public int money;
	public boolean alive = true;
}

package net.mgsx.game.examples.convoy.model;

import com.badlogic.gdx.graphics.Color;

public class MaterialType {
	public static final MaterialType METAL = new MaterialType(){{
		name = "Metal";
		color = Color.ORANGE;
	}};
	public static final MaterialType WATER = new MaterialType(){{
		name = "Water";
		color = Color.CYAN;
	}};
	public static final MaterialType PLASTIC = new MaterialType(){{
		name = "Plastic";
		color = Color.YELLOW;
	}};
	public static final MaterialType [] ALL = {METAL, WATER, PLASTIC};
	
	public String name;
	public Color color;
	
	// WATER, PLASTIC
}

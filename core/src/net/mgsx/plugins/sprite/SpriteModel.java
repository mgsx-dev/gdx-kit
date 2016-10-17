package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.mgsx.core.components.Duplicable;

public class SpriteModel implements Component, Duplicable
{
	public Sprite sprite;

	@Override
	public Component duplicate() {
		SpriteModel model = new SpriteModel();
		model.sprite = new Sprite(sprite);
		return model;
	}

	
	
}

package net.mgsx.game.plugins.g2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("sprite")
public class SpriteModel implements Component, Duplicable
{
	
	public static ComponentMapper<SpriteModel> components = ComponentMapper.getFor(SpriteModel.class);
	
	public Sprite sprite;

	@Override
	public Component duplicate() {
		SpriteModel model = new SpriteModel();
		model.sprite = new Sprite(sprite);
		return model;
	}

	
	
}

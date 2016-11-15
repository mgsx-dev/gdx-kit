package net.mgsx.game.plugins.g2d.tools;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.helpers.EntityReloader;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

public class SpriteModelReloader extends EntityReloader<Texture>
{
	public SpriteModelReloader(Engine engine) 
	{
		super(engine, Family.all(SpriteModel.class).get());
	}

	@Override
	protected void reload(Entity entity, Texture asset) 
	{
		SpriteModel sprite = SpriteModel.components.get(entity);
		sprite.sprite.setTexture(asset);
	}

}

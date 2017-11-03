package net.mgsx.game.plugins.g2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

// TODO persist reference to animation ?
public class Animation2DComponent implements Component
{
	public Animation<Sprite> animation;
	public float time;
}

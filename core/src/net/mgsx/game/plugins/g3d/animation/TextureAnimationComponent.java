package net.mgsx.game.plugins.g3d.animation;

import com.badlogic.ashley.core.Component;

public class TextureAnimationComponent implements Component
{
	public float time;
	public float uOffset, vOffset;
	public float uPerSec = 0;
	public float vPerSec = 0;
}

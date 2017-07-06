package net.mgsx.game.plugins.bullet;

import com.badlogic.gdx.physics.bullet.Bullet;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.bullet.components.BulletComponent;
import net.mgsx.game.plugins.bullet.listeners.BulletHeightFieldListener;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@PluginDef(components=BulletComponent.class, requires="com.badlogic.gdx.physics.bullet.Bullet")
public class BulletPlugin implements Plugin
{
	@Override
	public void initialize(GameScreen engine) 
	{
		// TODO in a system or something ?!
		Bullet.init();
		
		engine.entityEngine.addSystem(new BulletWorldSystem());
		
		engine.entityEngine.addSystem(new BulletHeightFieldListener());
	}

}

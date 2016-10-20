package net.mgsx.game.core.plugins;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public interface Initializable {
	public void initialize(Engine manager, Entity entity);
}

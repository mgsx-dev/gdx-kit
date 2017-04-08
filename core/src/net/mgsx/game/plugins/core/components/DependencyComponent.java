package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Use to tag entities having dependency links (forward or backward)
 * It helps {@link net.mgsx.game.plugins.core.systems.DependencySystem} in
 * filtering.
 * 
 * Dependenciy graph is not stored in components but is managed in system.
 * 
 * @author mgsx
 *
 */
public class DependencyComponent implements Component {
	
	public final static ComponentMapper<DependencyComponent> components = ComponentMapper
			.getFor(DependencyComponent.class);
}

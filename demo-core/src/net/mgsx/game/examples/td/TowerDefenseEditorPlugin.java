package net.mgsx.game.examples.td;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.td.systems.EnemyAnalysisSystem;
import net.mgsx.game.examples.td.systems.EnemyLogicSystem;
import net.mgsx.game.examples.td.systems.EnemyRenderer;
import net.mgsx.game.examples.td.systems.EntryRenderer;
import net.mgsx.game.examples.td.systems.FreezeSystem;
import net.mgsx.game.examples.td.systems.FrozenRender;
import net.mgsx.game.examples.td.systems.FrozenSystem;
import net.mgsx.game.examples.td.systems.HomeRenderer;
import net.mgsx.game.examples.td.systems.LazerRender;
import net.mgsx.game.examples.td.systems.LifeRenderer;
import net.mgsx.game.examples.td.systems.LifeSystem;
import net.mgsx.game.examples.td.systems.MapRenderer;
import net.mgsx.game.examples.td.systems.MapSystem;
import net.mgsx.game.examples.td.systems.PathFollowerSystem;
import net.mgsx.game.examples.td.systems.PrickleRender;
import net.mgsx.game.examples.td.systems.PrickleSystem;
import net.mgsx.game.examples.td.systems.ShotRender;
import net.mgsx.game.examples.td.systems.ShotSystem;
import net.mgsx.game.examples.td.systems.SingleTargetSystem;
import net.mgsx.game.examples.td.systems.TileRenderer;
import net.mgsx.game.examples.td.systems.TowerLogicSystem;
import net.mgsx.game.examples.td.systems.TowerRangeRenderer;
import net.mgsx.game.examples.td.systems.TowerRender;
import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.examples.td.tools.PlatformTool;
import net.mgsx.game.examples.td.tools.RoadTool;
import net.mgsx.game.examples.td.tools.TileSelector;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class TowerDefenseEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addSelector(new TileSelector(editor));

		editor.addTool(new RoadTool(editor));
		editor.addTool(new PlatformTool(editor));
		
		editor.entityEngine.addSystem(new WaveSystem());
		editor.entityEngine.addSystem(new MapSystem());
		editor.entityEngine.addSystem(new FreezeSystem());
		
		// enemy analysis before tower logic
		editor.entityEngine.addSystem(new EnemyAnalysisSystem());
		editor.entityEngine.addSystem(new TowerLogicSystem());
		editor.entityEngine.addSystem(new ShotSystem());

		editor.entityEngine.addSystem(new PrickleSystem());
		editor.entityEngine.addSystem(new FrozenSystem());
		editor.entityEngine.addSystem(new EnemyLogicSystem());
		
		editor.entityEngine.addSystem(new LifeSystem());
		editor.entityEngine.addSystem(new SingleTargetSystem());
		
		editor.entityEngine.addSystem(new PathFollowerSystem());

		
		// render ground
		editor.entityEngine.addSystem(new TileRenderer(editor.game));
		editor.entityEngine.addSystem(new PrickleRender(editor.game));
		
		editor.entityEngine.addSystem(new MapRenderer(editor.game));
		
		// render buildings
		editor.entityEngine.addSystem(new TowerRender(editor.game));
		editor.entityEngine.addSystem(new HomeRenderer(editor.game));
		editor.entityEngine.addSystem(new EntryRenderer(editor.game));
		
		// render dynamics
		editor.entityEngine.addSystem(new EnemyRenderer(editor.game));
		editor.entityEngine.addSystem(new ShotRender(editor.game));
		editor.entityEngine.addSystem(new LazerRender(editor.game));
		
		// render status
		editor.entityEngine.addSystem(new LifeRenderer(editor.game));
		editor.entityEngine.addSystem(new FrozenRender(editor.game));
		
		editor.entityEngine.addSystem(new TowerRangeRenderer(editor.game));
		
	}

}

package net.mgsx.game.examples.td;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.td.systems.EnemyLogicSystem;
import net.mgsx.game.examples.td.systems.EnemyRenderer;
import net.mgsx.game.examples.td.systems.EntryRenderer;
import net.mgsx.game.examples.td.systems.HomeRenderer;
import net.mgsx.game.examples.td.systems.LifeRenderer;
import net.mgsx.game.examples.td.systems.MapRenderer;
import net.mgsx.game.examples.td.systems.MapSystem;
import net.mgsx.game.examples.td.systems.ShotSystem;
import net.mgsx.game.examples.td.systems.TileRenderer;
import net.mgsx.game.examples.td.systems.TowerLogicSystem;
import net.mgsx.game.examples.td.systems.TowerRangeRenderer;
import net.mgsx.game.examples.td.systems.TowerRender;
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
		
		editor.entityEngine.addSystem(new MapSystem());
		editor.entityEngine.addSystem(new EnemyLogicSystem());
		editor.entityEngine.addSystem(new TowerLogicSystem());
		
		editor.entityEngine.addSystem(new TileRenderer(editor.game));
		editor.entityEngine.addSystem(new MapRenderer(editor.game));
		editor.entityEngine.addSystem(new TowerRender(editor.game));
		editor.entityEngine.addSystem(new HomeRenderer(editor.game));
		editor.entityEngine.addSystem(new EntryRenderer(editor.game));
		editor.entityEngine.addSystem(new EnemyRenderer(editor.game));
		editor.entityEngine.addSystem(new ShotSystem(editor.game));
		editor.entityEngine.addSystem(new LifeRenderer(editor.game));
		
		editor.entityEngine.addSystem(new TowerRangeRenderer(editor.game));
		
	}

}

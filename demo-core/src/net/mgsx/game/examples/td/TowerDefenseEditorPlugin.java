package net.mgsx.game.examples.td;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.td.components.Attachement;
import net.mgsx.game.examples.td.storage.AttachementSerializer;
import net.mgsx.game.examples.td.systems.ActiveRenderer;
import net.mgsx.game.examples.td.systems.AttachementSystem;
import net.mgsx.game.examples.td.systems.EmissiveSystem;
import net.mgsx.game.examples.td.systems.EnemyAnalysisSystem;
import net.mgsx.game.examples.td.systems.EnemyLogicSystem;
import net.mgsx.game.examples.td.systems.EnemyModelSystem;
import net.mgsx.game.examples.td.systems.EnemyRenderer;
import net.mgsx.game.examples.td.systems.EntryRenderer;
import net.mgsx.game.examples.td.systems.FollowRenderDebugSystem;
import net.mgsx.game.examples.td.systems.FollowRenderSystem;
import net.mgsx.game.examples.td.systems.FollowSystem;
import net.mgsx.game.examples.td.systems.FreezeSystem;
import net.mgsx.game.examples.td.systems.FrozenRender;
import net.mgsx.game.examples.td.systems.FrozenSystem;
import net.mgsx.game.examples.td.systems.HomeRenderer;
import net.mgsx.game.examples.td.systems.LazerRender;
import net.mgsx.game.examples.td.systems.LazerSystem;
import net.mgsx.game.examples.td.systems.LifeRenderer;
import net.mgsx.game.examples.td.systems.LifeSystem;
import net.mgsx.game.examples.td.systems.LoadSystem;
import net.mgsx.game.examples.td.systems.MapRenderer;
import net.mgsx.game.examples.td.systems.MapSystem;
import net.mgsx.game.examples.td.systems.MoveSystem;
import net.mgsx.game.examples.td.systems.NavSystem;
import net.mgsx.game.examples.td.systems.PathFollowerSystem;
import net.mgsx.game.examples.td.systems.PoisonSystem;
import net.mgsx.game.examples.td.systems.PrickleRender;
import net.mgsx.game.examples.td.systems.PrickleSystem;
import net.mgsx.game.examples.td.systems.PrioritySystem;
import net.mgsx.game.examples.td.systems.RangeSystem;
import net.mgsx.game.examples.td.systems.ShooterSystem;
import net.mgsx.game.examples.td.systems.ShotRender;
import net.mgsx.game.examples.td.systems.ShotRenderDebug;
import net.mgsx.game.examples.td.systems.ShotSystem;
import net.mgsx.game.examples.td.systems.SpeedSystem;
import net.mgsx.game.examples.td.systems.StunningSystem;
import net.mgsx.game.examples.td.systems.TargetSystem;
import net.mgsx.game.examples.td.systems.TileRenderer;
import net.mgsx.game.examples.td.systems.TowerDefenseHUD;
import net.mgsx.game.examples.td.systems.TowerLogicSystem;
import net.mgsx.game.examples.td.systems.TowerModelSystem;
import net.mgsx.game.examples.td.systems.TowerRangeRenderer;
import net.mgsx.game.examples.td.systems.TowerRangeRendererDebug;
import net.mgsx.game.examples.td.systems.TowerRender;
import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.examples.td.tools.AttachTool;
import net.mgsx.game.examples.td.tools.FollowPathTool;
import net.mgsx.game.examples.td.tools.FollowTool;
import net.mgsx.game.examples.td.tools.GameCamTool;
import net.mgsx.game.examples.td.tools.GameTool;
import net.mgsx.game.examples.td.tools.NavMeshDebugTool;
import net.mgsx.game.examples.td.tools.PlatformTool;
import net.mgsx.game.examples.td.tools.RoadTool;
import net.mgsx.game.examples.td.tools.TileSelector;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.g3d.systems.G3DTransformSystem;

public class TowerDefenseEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		// XXX disable confliting unused systems
		editor.entityEngine.removeSystem(editor.entityEngine.getSystem(G3DTransformSystem.class));
		
		editor.registry.addSerializer(Attachement.class, new AttachementSerializer());
		
		editor.addSelector(new TileSelector(editor));

		editor.addTool(new RoadTool(editor));
		editor.addTool(new PlatformTool(editor));
		editor.addTool(new FollowPathTool(editor));
		editor.addTool(new AttachTool(editor));
		editor.addTool(new FollowTool(editor));
		editor.addTool(new GameTool(editor));
		editor.addTool(new NavMeshDebugTool(editor));
		editor.addTool(new GameCamTool(editor));
		
		editor.entityEngine.addSystem(new WaveSystem());
		editor.entityEngine.addSystem(new MapSystem());
		editor.entityEngine.addSystem(new FreezeSystem());
		
		editor.entityEngine.addSystem(new LoadSystem());
		
		// enemy analysis before tower logic
		editor.entityEngine.addSystem(new EnemyAnalysisSystem());
		
		// first range system to invalidate some targets
		editor.entityEngine.addSystem(new RangeSystem());
		
		// priority logic may change here and invalidate target. (TODO create target check GamePipeline entry ??)
		editor.entityEngine.addSystem(new PrioritySystem());
		
		// then targetting system to select target
		editor.entityEngine.addSystem(new TargetSystem());
		
		editor.entityEngine.addSystem(new MoveSystem());
		
		editor.entityEngine.addSystem(new AttachementSystem());
		
		editor.entityEngine.addSystem(new TowerLogicSystem());
		editor.entityEngine.addSystem(new LoadSystem());
		editor.entityEngine.addSystem(new ShooterSystem());
		editor.entityEngine.addSystem(new LazerSystem());
		
		editor.addSystem(new TowerModelSystem());
		editor.addSystem(new EnemyModelSystem());
		
		editor.entityEngine.addSystem(new ShotSystem());

		editor.entityEngine.addSystem(new PrickleSystem());
		editor.entityEngine.addSystem(new FrozenSystem());
		editor.entityEngine.addSystem(new StunningSystem());
		editor.entityEngine.addSystem(new NavSystem());
		
		editor.entityEngine.addSystem(new SpeedSystem());
		
		editor.entityEngine.addSystem(new EnemyLogicSystem());
		
		editor.entityEngine.addSystem(new PoisonSystem());
		editor.entityEngine.addSystem(new LifeSystem());
		
		editor.entityEngine.addSystem(new PathFollowerSystem());
		editor.entityEngine.addSystem(new FollowSystem());
		
		editor.entityEngine.addSystem(new EmissiveSystem());

		
		// render ground
		editor.entityEngine.addSystem(new TileRenderer(editor.game));
		editor.entityEngine.addSystem(new PrickleRender(editor.game));
		
		editor.entityEngine.addSystem(new MapRenderer(editor.game));
		
		// render buildings
		editor.entityEngine.addSystem(new HomeRenderer(editor.game));
		editor.entityEngine.addSystem(new EntryRenderer(editor.game));
		
		// Render links
		editor.entityEngine.addSystem(new FollowRenderSystem(editor.game));

		// render dynamics
		editor.entityEngine.addSystem(new ShotRenderDebug(editor.game));
		editor.entityEngine.addSystem(new LazerRender(editor.game));
		editor.entityEngine.addSystem(new TowerRender(editor.game));
		editor.entityEngine.addSystem(new EnemyRenderer(editor.game));
		editor.entityEngine.addSystem(new ShotRender(editor.game));
		
		editor.entityEngine.addSystem(new ActiveRenderer(editor.game));
		
		// render status
		editor.entityEngine.addSystem(new LifeRenderer(editor.game));
		editor.entityEngine.addSystem(new FrozenRender(editor.game));
		
		editor.entityEngine.addSystem(new TowerRangeRenderer(editor.game));
		
		editor.entityEngine.addSystem(new TowerDefenseHUD(editor.game));
		
		editor.entityEngine.addSystem(new FollowRenderDebugSystem(editor.game));
		editor.entityEngine.addSystem(new TowerRangeRendererDebug(editor.game));
		
		// XXX default disable some systems
		editor.entityEngine.getSystem(ShotRenderDebug.class).setProcessing(false);
		editor.entityEngine.getSystem(TowerRender.class).setProcessing(false);
		editor.entityEngine.getSystem(EnemyRenderer.class).setProcessing(false);
		editor.entityEngine.getSystem(LifeSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(FollowRenderDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(TowerRangeRendererDebug.class).setProcessing(false);
		
	}

}

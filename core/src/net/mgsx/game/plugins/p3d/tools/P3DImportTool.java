package net.mgsx.game.plugins.p3d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.plugins.core.components.Transform3DComponent;
import net.mgsx.game.plugins.p3d.components.Particle3DComponent;
import net.mgsx.game.plugins.p3d.systems.Particle3DSystem;

public class P3DImportTool extends ClickTool {
	String fileName;

	@Inject Particle3DSystem system;
	
	public P3DImportTool(EditorScreen editor) {
		super("Add particles 3D", editor);
	}

	@Override
	protected void activate() {
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				fileName = file.path();
				editor.loadAssetNow(fileName, ParticleEffect.class);
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("p3d") || file.extension().equals("pfx");
			}
			@Override
			public String description() {
				return "Particle 3D files (p3d, pfx)";
			}
		});
	}

	@Override
	protected void create(Vector2 position)
	{
		Particle3DComponent pfx = getEngine().createComponent(Particle3DComponent.class);
		pfx.effectModel = editor.assets.get(fileName, ParticleEffect.class);
		
		Transform3DComponent transform = getEngine().createComponent(Transform3DComponent.class);
		transform.position.set(position, 0);
		transform.rotation.idt();
		
		Entity entity = currentEntity();
		entity.add(pfx).add(transform);
	}
}
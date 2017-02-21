package net.mgsx.game.plugins.particle2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

public class P2DImportTool extends ClickTool {
	String fileName;

	public P2DImportTool(EditorScreen editor) {
		super("Add particles", editor);
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
				return file.extension().equals("p");
			}
			@Override
			public String description() {
				return "Particle files (p)";
			}
		});
	}

	@Override
	protected void create(Vector2 position)
	{
		Particle2DComponent model = new Particle2DComponent();
		model.reference = fileName;
		model.position.set(position);
		Entity entity = currentEntity();
		Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
		transform.position.set(position);
		entity.add(model).add(transform);
	}
}
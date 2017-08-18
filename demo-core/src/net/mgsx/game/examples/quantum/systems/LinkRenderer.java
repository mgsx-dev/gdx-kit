package net.mgsx.game.examples.quantum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.quantum.components.Link;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class LinkRenderer extends IteratingSystem
{
	private ShapeRenderer renderer;
	EditorScreen editor;
	public LinkRenderer(EditorScreen editor) {
		super(Family.all(Link.class).get(), GamePipeline.RENDER);
		this.editor = editor;
		this.renderer = new ShapeRenderer();
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.setProjectionMatrix(editor.game.camera.combined);
		renderer.begin(ShapeType.Line);
		super.update(deltaTime);
		renderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Link link = Link.components.get(entity);
		if(link.source == null || link.target == null) return;
		
		Transform2DComponent source = Transform2DComponent.components.get(link.source);
		Transform2DComponent target = Transform2DComponent.components.get(link.target);
		
		renderer.line(source.position, target.position);
	}
}

package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class TowerRangeRenderer extends AbstractSpriteSystem
{
	@Asset("perlin.png")
	public Texture texture;
	
	@Editable
	public ShaderProgram shader;

	public TowerRangeRenderer(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Range.class).get(), GamePipeline.RENDER_TRANSPARENT);
		shader = ShaderProgramHelper.reload(shader,
				Gdx.files.internal("td/shaders/range-vertex.glsl"),
				Gdx.files.internal("td/shaders/range-fragment.glsl"));
	}
	
	@Override
	public void update(float deltaTime) {
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE); //_MINUS_SRC_ALPHA);
		batch.setShader(shader);
		sprite.setTexture(texture);
		sprite.setU(-1);
		sprite.setU2(1);
		sprite.setV(-1);
		sprite.setV2(1);
		//sprite.setRegion(0, 0, 1, 1);
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Range range = Range.components.get(entity);
		sprite.setBounds(transform.position.x - range.distance, transform.position.y - range.distance, range.distance * 2, range.distance * 2);
		sprite.draw(batch);
	}
}

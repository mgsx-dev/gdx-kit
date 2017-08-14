package net.mgsx.game.core.helpers.shaders;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.kit.test.KitSystemTest;

@Storable
@EditableSystem
public class ShaderProgramManagedTest extends EntitySystem {

	public static void main(String[] args) {
		new KitSystemTest(new ShaderProgramManagedTest());
	}
	
	@ShaderInfo(
		vs="net/mgsx/game/core/helpers/shaders/test.vs",
		fs="net/mgsx/game/core/helpers/shaders/test.fs")
	public static class CustomShader extends ShaderProgramManaged
	{
		@Uniform(only="hd") @Editable(type=EnumType.UNIT) public float luminosity = 1f;
	}
	
	@Editable public CustomShader customShader = new CustomShader();
	
	private ShapeRenderer renderer;
	
	public ShaderProgramManagedTest() {
		super(GamePipeline.RENDER);
	}
	
	@Override
	public void update(float deltaTime) {
		if(customShader.begin()){
			if(renderer != null) renderer.dispose();
			renderer = new ShapeRenderer(6, customShader.program());
		}
		
		renderer.begin(ShapeType.Filled);
		renderer.rect(10, 10, 400, 400);
		renderer.end();
	}
}

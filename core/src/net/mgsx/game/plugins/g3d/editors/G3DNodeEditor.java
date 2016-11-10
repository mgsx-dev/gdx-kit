package net.mgsx.game.plugins.g3d.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class G3DNodeEditor implements EntityEditorPlugin
{

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		// TODO check animations
		final G3DModel model = entity.getComponent(G3DModel.class);
		
//		// XXX patch shader for a node
//		Node node = model.modelInstance.getNode("Cylinder.001", true);
//		// model.modelInstance.userData.
//		if(node != null){
//			final NodePart blockPart = node.parts.get(0);
//			
//	        final TextureAttribute ta = (TextureAttribute)blockPart.material.get(TextureAttribute.Diffuse);
//	        final ColorAttribute ca = (ColorAttribute)blockPart.material.get(ColorAttribute.Emissive);
//	        model.customAnimation = new Animator(){
//	        	@Override
//	        	public void animate(float deltaTime) {
//	        		
//	        		ca.color.set(1,0,0,1);
//	        		((ColorAttribute)blockPart.material.get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
//	        		((ColorAttribute)blockPart.material.get(ColorAttribute.Ambient)).color.set(Color.WHITE);
//	        		ta.offsetU = 0;
//	        		ta.offsetV -= deltaTime * 1; 
//	        		ta.scaleU = 1;
//	        		// XXX speed
//	        	}
//	        };
//		}
		
		// build animation selector
		final SelectBox<String> animationSelector = new SelectBox<String>(skin);
		Array<String> animations = new Array<String>();
		animations.add(""); // default off value
		for(Animation animation : model.modelInstance.animations){
			animations.add(animation.id);
		}
		animationSelector.setItems(animations);
		animationSelector.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String selection = animationSelector.getSelected();
				if(selection.isEmpty())
					model.animationController.paused = true;
				else{
					model.animationController.allowSameAnimation = true; // XXX at init time !
					model.animationController.paused = false;
					model.animationController.setAnimation(selection, -1); // loop
				}
			}
		});
		
		Table main = new Table(skin);
		
//		main.add("Name");
//		main.add(model.node.id);
//		main.row();
		
		main.add("Animation");
		main.add(animationSelector);
		main.row();
		
		
		final Button btBlend = EntityEditor.createBoolean(skin, model.blended);
		
		main.add("Blended");
		main.add(btBlend);
		main.row();
		
		
		btBlend.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(model.blended != btBlend.isChecked()){
					model.blended = btBlend.isChecked();
					model.applyBlending();
				}
			}
		});

		final Button btCulling = EntityEditor.createBoolean(skin, model.culling);
		
		main.add("Culling");
		main.add(btCulling);
		main.row();
		
		
		btCulling.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				model.culling = btCulling.isChecked();
			}
		});

//		main.add("Parts");
//		main.add(model.node.parts == null ? "none" : String.valueOf(model.node.parts.size));
//		main.row();
		
		
		return main;
	}

}

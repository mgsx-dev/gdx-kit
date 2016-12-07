package net.mgsx.game.plugins.g3d.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class ModelAnimationSelector implements FieldEditor
{
	private Entity entity;
	
	public ModelAnimationSelector(Entity entity) {
		super();
		this.entity = entity;
	}

	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		final G3DModel model = G3DModel.components.get(entity);
		if(model != null){
			
			final SelectBox<String> animationSelector = new SelectBox<String>(skin);
			Array<String> animations = new Array<String>();
			animations.add(""); // default off value
			for(Animation animation : model.modelInstance.animations){
				animations.add(animation.id);
			}
			animationSelector.setItems(animations);
			Animation animation = (Animation)accessor.get();
			if(animation != null) animationSelector.setSelected(animation.id);
			animationSelector.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					String selection = animationSelector.getSelected();
					if(selection.isEmpty())
						accessor.set(null);
					else{
						accessor.set(model.modelInstance.getAnimation(selection));
					}
				}
			});
			
			return animationSelector;
		}
		return null;
	}

}

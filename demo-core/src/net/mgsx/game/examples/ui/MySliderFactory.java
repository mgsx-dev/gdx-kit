package net.mgsx.game.examples.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.plugins.ui.ScalarComponent;
import net.mgsx.game.plugins.ui.WidgetFactory;

public class MySliderFactory implements WidgetFactory
{
	@Override
	public Actor createActor(Engine engine, final Entity entity, Skin skin) {
		entity.add(engine.createComponent(ScalarComponent.class));
		
		Table table = new Table(skin);
		table.add("My Custom Slider");
		final Slider slider = new Slider(0, 1, .001f, false, skin);
		table.add(slider);
		
		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScalarComponent scalar = ScalarComponent.components.get(entity);
				scalar.value = slider.getValue();
			}
		});
		
		return table;
	}
}

package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public interface WidgetFactory {

	Actor createActor(Engine engine, Entity entity, Skin skin);

}

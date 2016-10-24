package net.mgsx.game.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.model.Box2DFixtureModel;

public class Box2DBodyEditorPlugin implements EntityEditorPlugin
{

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		Box2DBodyModel model = entity.getComponent(Box2DBodyModel.class);
		
		Table table = new Table(skin);
		
		table.add(new EntityEditor(model.body, skin));
		
		for(Box2DFixtureModel fix : model.fixtures){
			table.row();
			table.add(new EntityEditor(fix, skin));
		}
		
		return table;
		
	}

}

package net.mgsx.game.plugins.box2d.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;

public class Box2DBodyEditorPlugin implements EntityEditorPlugin
{
	@Editable
	public static class FixtureEditor
	{
		private Box2DFixtureModel model;
		
		public FixtureEditor(Box2DFixtureModel model) {
			super();
			this.model = model;
			Filter filter = model.fixture.getFilterData();
			categoryBits = filter.categoryBits;
			maskBits = filter.maskBits;
			groupIndex = filter.groupIndex;
			
		}

		@Editable(type=EnumType.BITS)
		public short categoryBits;
		
		@Editable(type=EnumType.BITS)
		public short maskBits;
		
		@Editable
		public short groupIndex;
		
		@Editable
		public void applyFilter(){
			Filter filter = model.fixture.getFilterData();
			filter.categoryBits = categoryBits;
			filter.maskBits = maskBits;
			filter.groupIndex = groupIndex;
			model.fixture.setFilterData(filter);
		}
		@Editable
		public void applyFilterDef(){
			Filter filter = model.def.filter;
			filter.categoryBits = categoryBits;
			filter.maskBits = maskBits;
			filter.groupIndex = groupIndex;
		}
	}
	
	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		Box2DBodyModel model = entity.getComponent(Box2DBodyModel.class);
		
		Table table = new Table(skin);
		
		table.add(new EntityEditor(model.body, skin));
		
		for(final Box2DFixtureModel fix : model.fixtures){
			table.row();
			
			FixtureEditor fixtureEditor = new FixtureEditor(fix);
			
			table.add(new EntityEditor(fixtureEditor, true, skin));
			
			table.row();
			table.add(new EntityEditor(fix, skin));
		}
		
		return table;
		
	}

}

package net.mgsx.box2d.editor;

import com.badlogic.gdx.math.Vector2;

import net.mgsx.box2d.editor.Box2DPresets.Box2DPreset;
import net.mgsx.box2d.editor.Box2DPresets.Items;
import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.Command;

public class Box2DCommands {

	public static Command preset(final WorldItem worldItem, final Box2DPreset preset, final float x, final float y){
		return new Command(){
			private Items items;
			@Override
			public void commit() 
			{
				items = new Items();
				preset.create(items, worldItem.world, x, y);
				worldItem.addAll(items);
			}
			@Override
			public void rollback() {
				worldItem.destroy(items);
			}
		};
	}
	
	// TODO maybe create a special Vector2 mutator ... ?, same for float ... and Vec3
	public static Command moveBody(final BodyItem body, final Vector2 value){
		return new Command(){
			Vector2 position;
			@Override
			public void commit() {
				position = body.body.getTransform().getPosition();
				body.body.getTransform().setPosition(value);
			}
			@Override
			public void rollback() {
				body.body.getTransform().setPosition(position);
			}
		};
	}
}

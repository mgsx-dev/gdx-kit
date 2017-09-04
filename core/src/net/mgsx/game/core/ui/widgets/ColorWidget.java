package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.helpers.ColorHelper;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorWrapper;
import net.mgsx.game.core.ui.accessors.FieldAccessorWrapper;

public class ColorWidget implements FieldEditor
{
	public static final ColorWidget instance = new ColorWidget();
	
	@Override
	public Actor create(Accessor accessor, Skin skin) 
	{
		Table table = new Table(skin);
		
		final TextButton btRGB = new TextButton("RGB", skin, "toggle");
		final TextButton btHSV = new TextButton("HSV", skin, "toggle");
		
		new ButtonGroup<TextButton>(btRGB, btHSV);

		final UpdatableTable rgbControl = createRGB(accessor, skin);
		final UpdatableTable hsvControl = createHSV(accessor, skin);
		
		btRGB.setChecked(true);
		final Cell cell = table.add(rgbControl);
		
		table.add(btRGB);
		table.add(btHSV);
		
		btRGB.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btRGB.isChecked()){
					rgbControl.update();
					cell.setActor(rgbControl);
				}
			}
		});
		
		btHSV.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(btHSV.isChecked()){
					hsvControl.update();
					cell.setActor(hsvControl);
				}
			}
		});
		
		return table;
	}
	
	private UpdatableTable createRGB(Accessor accessor, Skin skin){
		Color c = (Color)accessor.get();
		
		final FloatWidget r = createSlider(accessor, c, new FieldAccessorWrapper(accessor, "r"), skin);
		final FloatWidget g = createSlider(accessor, c, new FieldAccessorWrapper(accessor, "g"), skin);
		final FloatWidget b = createSlider(accessor, c, new FieldAccessorWrapper(accessor, "b"), skin);
		final FloatWidget a = createSlider(accessor, c, new FieldAccessorWrapper(accessor, "a"), skin);
		
		UpdatableTable sub = new UpdatableTable(skin){
			@Override
			protected void update() {
				r.updateValue();
				g.updateValue();
				b.updateValue();
				a.updateValue();
			}
		};
		sub.add("R");
		sub.add(r);
		sub.add("G");
		sub.add(g);
		sub.add("B");
		sub.add(b);
		sub.add("A");
		sub.add(a);
		return sub;
	}

	private static class HSVWrapper extends AccessorWrapper{

		private int index;
		private float[] hsv;

		public HSVWrapper(Accessor original, float [] hsv, int index) {
			super(original);
			this.hsv = hsv;
			this.index = index;
		}
		
		@Override
		public void set(Object value) {
			hsv[index] = (Float)value;
			ColorHelper.hsvToColor(original.get(Color.class), hsv);
		}
		@Override
		public Object get() {
			ColorHelper.colorToHsv(hsv, original.get(Color.class));
			return hsv[index];
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public Class getType() {
			return float.class;
		}
		
	}
	
	private abstract static class UpdatableTable extends Table{
		
		public UpdatableTable(Skin skin) {
			super(skin);
		}

		abstract protected void update();
	}
	
	private UpdatableTable createHSV(Accessor accessor, Skin skin){
		Color c = (Color)accessor.get();
		
		final float [] hsv = new float[3];
		
		final FloatWidget h = createSlider(accessor, c, new HSVWrapper(accessor, hsv, 0), skin);
		final FloatWidget s = createSlider(accessor, c, new HSVWrapper(accessor, hsv, 1), skin);
		final FloatWidget v = createSlider(accessor, c, new HSVWrapper(accessor, hsv, 2), skin);
		final FloatWidget a = createSlider(accessor, c, new FieldAccessorWrapper(accessor, "a"), skin);
		
		UpdatableTable sub = new UpdatableTable(skin){
			@Override
			protected void update() {
				h.updateValue();
				s.updateValue();
				v.updateValue();
				a.updateValue();
			}
		};
		
		sub.add("H");
		sub.add(h);
		sub.add("S");
		sub.add(s);
		sub.add("V");
		sub.add(v);
		sub.add("A");
		sub.add(a);
		
		return sub;
	}

	private FloatWidget createSlider(Accessor rootField, Color c, Accessor accessor, Skin skin) {
		boolean dynamic = rootField.config() != null && rootField.config().realtime();
		boolean readonly = rootField.config() != null && rootField.config().readonly();
		final FloatWidget label = new FloatWidget(accessor, dynamic, readonly, skin);
		return label;
	}

}

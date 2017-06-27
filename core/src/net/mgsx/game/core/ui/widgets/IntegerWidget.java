package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.ui.FieldEditor;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.AccessorHelper;

public class IntegerWidget implements FieldEditor
{
	public static final IntegerWidget unlimited = new IntegerWidget();
	public static final IntegerWidget signedByte = new IntegerWidget(8, true);
	public static final IntegerWidget unsignedByte = new IntegerWidget(8, false);
	public static final IntegerWidget signedShort = new IntegerWidget(16, true);
	public static final IntegerWidget unsignedShort = new IntegerWidget(16, false);
	public static final IntegerWidget signedInt = new IntegerWidget(32, true);
	public static final IntegerWidget unsignedInt = new IntegerWidget(32, false);
	public static final IntegerWidget signedLong = new IntegerWidget(64, true);
	public static final IntegerWidget unsignedLong = new IntegerWidget(64, false);
	
	private Long min, max;
	
	public IntegerWidget() {
		this(null, null);
	}
	
	public IntegerWidget(Long min, Long max) {
		super();
		this.min = min;
		this.max = max;
	}
	
	public IntegerWidget(int bits, boolean signed){
		this(signed ? -(1L<<(bits-1)) : 0L, signed ? 1L<<(bits-1)-1L : 1L<<bits-1L);
	}


	@Override
	public Actor create(final Accessor accessor, Skin skin) 
	{
		if(accessor.config() != null && accessor.config().readonly()){
			Label label = createReadOnly(accessor, skin);
			label.setAlignment(Align.center);
			label.setColor(Color.CYAN);
			return label;
		}else{
			return createEditable(accessor, skin);
		}
	}
	
	private Label createReadOnly(final Accessor accessor, Skin skin){
		String initText = String.valueOf(AccessorHelper.asLong(accessor));
		if(accessor.config() != null && accessor.config().realtime()){
			return new Label(initText, skin){
				private long oldValue = AccessorHelper.asLong(accessor);
				@Override
				public void act(float delta) {
					long newValue = AccessorHelper.asLong(accessor);
					if(newValue != oldValue){
						setText(String.valueOf(newValue));
						oldValue = newValue;
					}
					super.act(delta);
				}
			};
		}else{
			return new Label(initText, skin);
		}
	}
	
	private Actor createEditable(final Accessor accessor, Skin skin){
		Table sub = new Table(skin);
		final Label label = new Label("", skin);
		final TextButton btPlus = new TextButton("+", skin);
		final TextButton btMinus = new TextButton("-", skin);
		final TextButton btDiv = new TextButton("/", skin);
		final TextButton btMul = new TextButton("*", skin);
		
		label.setAlignment(Align.center);
		
		if(accessor.config() != null && accessor.config().type() == EnumType.RANDOM){
			label.setText(String.format("0x%x", accessor.get()));
			final TextButton btRnd = new TextButton("randomize", skin);
			sub.add(label).pad(4);
			sub.add(btRnd);
			btRnd.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					long value = MathUtils.random(Long.MAX_VALUE);
					AccessorHelper.fromLong(accessor, value);
					label.setText(String.format("0x%x", value));
				}
			});
		}else{
			label.setText(String.valueOf(accessor.get()));
			sub.add(btDiv);
			sub.add(btMinus);
			sub.add(label).pad(4);
			sub.add(btPlus);
			sub.add(btMul);
			
			btPlus.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					long value = 1 + AccessorHelper.asLong(accessor);
					if(max == null || value <= max){
						AccessorHelper.fromLong(accessor, value);
						label.setText(String.valueOf(value));
					}
				}
			});
			btMinus.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					long value = -1 + AccessorHelper.asLong(accessor);
					if(min == null || value >= min){
						AccessorHelper.fromLong(accessor, value);
						label.setText(String.valueOf(value));
					}
				}
			});
			btDiv.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					long value = AccessorHelper.asLong(accessor) / 2;
					if(max == null || value <= max){
						AccessorHelper.fromLong(accessor, value);
						label.setText(String.valueOf(value));
					}
				}
			});
			btMul.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					long value = 2 * AccessorHelper.asLong(accessor);
					if(min == null || value >= min){
						AccessorHelper.fromLong(accessor, value);
						label.setText(String.valueOf(value));
					}
				}
			});
		}
		
		
		return sub;
	}
	
}

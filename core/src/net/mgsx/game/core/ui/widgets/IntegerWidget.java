package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

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
		Table sub = new Table(skin);
		final Label label = new Label("", skin);
		final TextButton btPlus = new TextButton("+", skin);
		final TextButton btMinus = new TextButton("-", skin);
		final TextButton btDiv = new TextButton("/", skin);
		final TextButton btMul = new TextButton("*", skin);
		sub.add(btDiv);
		sub.add(btMinus);
		sub.add(label).pad(4);
		sub.add(btPlus);
		sub.add(btMul);
		label.setText(String.valueOf(accessor.get()));
		
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
		
		return sub;
	}
	
}

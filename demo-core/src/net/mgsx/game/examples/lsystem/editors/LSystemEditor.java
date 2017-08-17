package net.mgsx.game.examples.lsystem.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.accessors.FieldAccessor;
import net.mgsx.game.core.ui.widgets.FloatWidget;
import net.mgsx.game.examples.lsystem.components.LSystem3D;
import net.mgsx.game.examples.lsystem.model.Formula;
import net.mgsx.game.examples.lsystem.model.FormulaParser;
import net.mgsx.game.examples.lsystem.model.LSystem;
import net.mgsx.game.examples.lsystem.model.Parameter;
import net.mgsx.game.examples.lsystem.model.Rule;
import net.mgsx.game.examples.lsystem.model.l2d.Rules2D;

public class LSystemEditor implements EntityEditorPlugin
{
	private EditorScreen editor;
	
	public LSystemEditor(EditorScreen editor) {
		super();
		this.editor = editor;
	}

	@Override
	public Actor createEditor(Entity entity, Skin skin) 
	{
		LSystem3D lsystem = LSystem3D.components.get(entity);
		
		if(lsystem.system == null){
			LSystem system = lsystem.system = new LSystem();
			// s.rule("S", Rules3D.size);
			system.rule("+", Rules2D.rotateLeft);
			system.rule("-", Rules2D.rotateRight);
			system.rule("O", Rules2D.circle);
			system.rule("G", Rules2D.circle);
			system.rule("F", Rules2D.line);
			system.rule("L", Rules2D.line);
			system.rule("S", Rules2D.size);
			system.rule("[", Rule.push);
			system.rule("]", Rule.pop);
			system.rule("{", Rule.loopStart);
			system.rule("}", Rule.loopEnd);
			system.rule("r", Rules2D.color(Color.RED));
			system.rule("g", Rules2D.color(Color.GREEN));
			system.rule("b", Rules2D.color(Color.BLUE));
			system.rule("y", Rules2D.color(Color.YELLOW));
			system.rule("c", Rules2D.color(Color.CYAN));
			system.rule("p", Rules2D.color(Color.PURPLE));
			
			Formula f1 = new FormulaParser().parse(system, "F[+F]F[-F]F");
			system.substitutions.put("F", f1);
			f1.symbol = "F";
			
			Formula f2 = new FormulaParser().parse(system, "O");
			system.substitutions.put("G", f2);
			f1.symbol = "G";
			
			system.parameter("angle").value = 30;
			system.parameter("size").value = .5f;
		}
		
		// display rules ... (table add)
		
		// parse formula : generate missing symbol (float)
		Table root = new Table(skin);
		
		create(root, skin, lsystem);
		
		return root;
	}

	private void create(final Table root, final Skin skin, final LSystem3D lsystem)
	{
		for(Formula f : lsystem.system.substitutions()){
			create(root, skin, lsystem, f);
		}
		
		for(Parameter p : lsystem.system.parameters()){
			create(root, skin, lsystem, p);
		}
		
		
		TextButton bt = new TextButton("Apply", skin);
		root.add("Generation");
		root.add(bt).row();
		
		bt.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				generate(lsystem);
				root.clear();
				create(root, skin, lsystem);
			}});
	}

	private void create(Table root, Skin skin, LSystem3D lsystem, Parameter p) {
		root.add(p.name);
		root.add(new FloatWidget(new FieldAccessor(p, "value"), false, false, skin)).row();
	}

	private void generate(LSystem3D lsystem) 
	{
		lsystem.system.update();
	}

	private void create(final Table root, final Skin skin, final LSystem3D lsystem, final Formula f) 
	{
		Label label = new Label(f.symbol, skin);
		final TextField formula = new TextField(f.value, skin);
		formula.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				f.value = formula.getText();
			}
		});
		formula.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Input.Keys.ENTER) formula.getStage().setKeyboardFocus(null);
				return super.keyDown(event, keycode);
			}
		});
		root.add(label);
		root.add(formula).row();
	}


}

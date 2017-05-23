package net.mgsx.game.examples.tactics.systems;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.examples.tactics.tools.MapGeneratorTool.Node;
import net.mgsx.game.examples.tactics.tools.WorldCell;
import net.mgsx.game.examples.tactics.tools.WorldMap;
import net.mgsx.game.plugins.core.systems.HUDSystem;

public class TacticMapHUD extends HUDSystem
{
	@Asset("skins/game-skin.json")
	public Skin skin;
	
	private Table table;
	
	@Override
	protected void create() 
	{
		Table main = new Table();
		table = new Table(skin);
		main.add(table).expand().top().right();
		main.setFillParent(true);
		getStage().addActor(main);
	}

	public void setCellInfo(WorldMap map, Node node) 
	{
		table.clear();
		
		table.add(String.format("position : %d %d", node.ax, node.ay)).row();
		table.add(String.format("difficulty : %.1f", node.difficulty * 100)).row();
		
		// TODO calculate treasure / monster / chance
		RandomXS128 random = new RandomXS128(map.seed + node.ax);
		random.setSeed(random.nextLong() + node.ay);
		long nodeSeed = random.nextLong();
		
		table.add(String.format("discovered : %d / inf", map.discovered.size)).row();
		
		table.add(String.format("seed : 0x%x", nodeSeed)).row();
		table.add(node.isSea ? "Sea" : "Ground").row();
		
		float chance = 1 - random.nextFloat();
		boolean polarity = random.nextFloat() < .5f;
		boolean exists = random.nextFloat() < .5f;
		
		if(node.isCity){
			table.add("CITY : ...").row(); // TODO city name !
		}
		else if(exists || node.isMission){
			if(polarity){
				if(node.isMission)
					table.add("Relic MISSION").row();
				else
					table.add(String.format("Treasure : %d %%", MathUtils.round(chance * 100))).row();
			}else{
				if(node.isMission)
					table.add("Boss MISSION").row();
				else if(node.isSea)
					table.add(String.format("Damages : %d %%", MathUtils.round(chance * 100))).row();
				else
					table.add(String.format("Monster : %d %%", MathUtils.round(chance * 100))).row();
			}
		}else{
			table.add("nothing").row();
		}
		
		if(node.isDiscovered){
			table.add("already discovered").row();
		}
		else if(node.isMission || node.isCity || Math.random() < chance){
			table.add("DISCOVERED !!").row();
			map.discovered.add(new WorldCell(node.ax, node.ay));
			
			TextButton bt = new TextButton("!!! DISCOVERED !!!", skin);
			Table t = new Table(skin);
			t.add(bt).expand().center().right();
			t.setTransform(true);
			t.setScale(4);
			t.setOrigin(Align.right);
			// t.setOrigin(t.getWidth()/2, t.getHeight()/2);
			table.add(t).expand().center().row();
			//t.setFillParent(true);
			//getStage().addActor(t);
			
			t.addAction(Actions.sequence(Actions.scaleTo(1, 1, 1, Interpolation.bounceOut), Actions.delay(2), Actions.removeActor()));
		}
		
		
	}
}

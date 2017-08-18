package net.mgsx.game.examples.tactics.logic;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.examples.tactics.logic.BattleLogic.BattleListener;
import net.mgsx.game.examples.tactics.model.CardDef;
import net.mgsx.game.examples.tactics.model.CharacterDef;
import net.mgsx.game.examples.tactics.model.Model;
import net.mgsx.game.examples.tactics.model.TeamDef;

public class BattleLogicTest 
{
	private Model model;
	private BattleListener recorder;

	@Before
	public void setup(){
		model = Model.load(new FileHandle(new File("test/tactics-model.json")));
		recorder = new BattleListener() {
			@Override
			public void onPlayerChange(CharacterBattle old, CharacterBattle current) {
				System.out.println("change : " + current.def.id);
			}
			@Override
			public void onEffectApply(CharacterBattle target, EffectBattle fx) {
				System.out.println("fx apply to " + target.def.id);
			}
			@Override
			public void onDie(CharacterBattle target) {
				System.out.println("die " + target.def.id);
			}
			@Override
			public void onEnd(boolean victoryA, boolean victoryB) {
				System.out.println(victoryA ? "team A win" : "team A loose");
				System.out.println(victoryB ? "team B win" : "team B loose");
			}
			@Override
			public void onTarget(CardBattle card, CharacterBattle target) {
			}
			@Override
			public void onEffectBegin(CharacterBattle character, EffectBattle fx) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onEffectEnd(CharacterBattle character, EffectBattle fx) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	@Test
	public void test(){
		
		// create team A : 1 warrior
		// create team B : 1 bat
		
		TeamDef a = new TeamDef();
		a.characters.add(model.getCharacter("warrior"));
		
		TeamDef b = new TeamDef();
		b.characters.add(model.getCharacter("bat"));
		
		BattleLogic logic = BattleLogic.create(model, a, b);
		logic.listener = recorder;
		
		CharacterBattle hero = logic.teamA.characters.first();
		CharacterBattle bat = logic.teamB.characters.first();
		
		logic.nextTurn();

		Assert.assertSame(bat, logic.characters.first());

		logic.selectAction(bat.cards.first(), hero);
		
		logic.nextTurn();
		
		Assert.assertSame(hero, logic.characters.first());
		
		logic.selectAction(hero.cards.first(), bat);
		
		logic.nextTurn();
		
		Assert.assertEquals(0, bat.life);
		Assert.assertEquals(1, logic.characters.size);
		
		Assert.assertSame(hero, logic.characters.first());
		
		Assert.assertEquals(0, logic.teamB.characters.size);
		
	}
	
	@Test
	public void testPoison(){
		CharacterDef def = new CharacterDef();
		def.life = 18;
		def.cards.add("move");
		
		CardDef card = new CardDef();
		card.id = "move";
		card.wait = 100;
		model.cards.add(card);
		
		TeamDef a = new TeamDef();
		a.characters.add(def);
		
		TeamDef b = new TeamDef();
		
		BattleLogic logic = BattleLogic.create(model, a, b);
		logic.listener = recorder;
		
		EffectBattle fx = new EffectBattle();
		fx.life = -1;
		fx.turns = 20;
		logic.characters.first().effects.add(fx);
		
		logic.nextTurn();
		
		Assert.assertEquals(0, logic.teamA.characters.size);
	}
}

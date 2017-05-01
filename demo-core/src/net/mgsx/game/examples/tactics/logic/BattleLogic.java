package net.mgsx.game.examples.tactics.logic;

import java.util.Comparator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.tactics.model.Model;
import net.mgsx.game.examples.tactics.model.TeamDef;

public class BattleLogic {

	public static interface BattleListener
	{
		public void onEffectApply(CharacterBattle target, EffectBattle fx);
		public void onDie(CharacterBattle target);
		public void onPlayerChange(CharacterBattle old, CharacterBattle current);
		public void onEnd(boolean victoryA, boolean victoryB);
	}
	
	public BattleListener listener = new BattleListener() {
		@Override
		public void onPlayerChange(CharacterBattle old, CharacterBattle current) {
		}
		@Override
		public void onEffectApply(CharacterBattle target, EffectBattle fx) {
		}
		@Override
		public void onDie(CharacterBattle target) {
		}
		@Override
		public void onEnd(boolean victoryA, boolean victoryB) {
		}
	};
	
	
	private static Comparator<CardBattle> cardComparator = new Comparator<CardBattle>() {
		@Override
		public int compare(CardBattle o1, CardBattle o2) {
			return Float.compare(o1.turns, o2.turns);
		}
	};
	private static Comparator<CharacterBattle> characterComparator = new Comparator<CharacterBattle>() {
		@Override
		public int compare(CharacterBattle o1, CharacterBattle o2) {
			return Float.compare(o1.turns, o2.turns);
		}
	};
	
	public TeamBattle teamA, teamB;
	
	public Array<CharacterBattle> characters = new Array<CharacterBattle>();
	
	public CharacterBattle current;
	public Model model;
	
	public static BattleLogic create(Model model, TeamDef a, TeamDef b){
		BattleLogic battle = new BattleLogic();
		battle.model = model;
		battle.teamA = new TeamBattle(a);
		battle.teamB = new TeamBattle(b);
		battle.characters.addAll(battle.teamA.characters);
		battle.characters.addAll(battle.teamB.characters);
		return battle;
	}
	
	public void nextTurn()
	{
		if(characters.size <= 0) return;
		
		// random cards (XXX all cards for now !) :
		for(CharacterBattle character : characters)
		{
			if(character.cards.size <= 0){
				for(String id : character.def.cards){
					CardBattle card = new CardBattle(model.getCards(id));
					card.turns = card.def.wait;
					character.cards.add(card);
				}
			}
		}
		for(CharacterBattle character : characters)
		{
			character.cards.sort(cardComparator);
			CardBattle card = character.cards.first();
			character.turns = card.turns;
		}
		CharacterBattle old = current;
		characters.sort(characterComparator);
		current = characters.first();
		float turns = current.turns;
		
		System.out.println("turns passed : " + String.valueOf(turns));
		
		// update cards for the next step
		for(CharacterBattle character : characters)
		{
			for(CardBattle card : character.cards){
				card.turns -= turns;
			}
		}
		
		// resolve pending effects
		for(int i=0 ; i<characters.size ; )
		{
			CharacterBattle character = characters.get(i);
			for(int j=0 ; j<character.effects.size ; ){
				EffectBattle fx = character.effects.get(j);
				int consumedTurns = Math.max(1, (int)Math.min(turns, fx.turns));
				fx.turns -= consumedTurns;
				if(fx.life != 0){
					character.life = MathUtils.clamp(character.life + fx.life * consumedTurns, 0, character.def.life);
				}
				listener.onEffectApply(character, fx);
				if(fx.turns <= 0){
					character.effects.removeIndex(j);
				}else{
					j++;
				}
			}
			if(character.life <= 0){
				characters.removeIndex(i);
				teamA.characters.removeValue(character, true);
				teamB.characters.removeValue(character, true);
				listener.onDie(character);
			}else{
				i++;
			}
		}
		
		// select again to remove dead entities
		
		current = characters.size > 0 ? characters.first() : null;
		
		// if(old != current){
			listener.onPlayerChange(old, current);
		// }

		if(teamA.characters.size <= 0 || teamB.characters.size <= 0){
			listener.onEnd(teamA.characters.size > 0, teamB.characters.size > 0);
		}
	}
	
	public void selectAction(CardBattle card, CharacterBattle target){
		selectAction(card, new Array<CharacterBattle>(new CharacterBattle[]{target}));
	}
	public void selectAction(CardBattle card, Array<CharacterBattle> targets)
	{
		for(CharacterBattle target : targets){
			EffectBattle fx = new EffectBattle();
			if(card.def.dmg != null){
				if(card.def.dmg.min < 0)
					fx.life = MathUtils.random(-card.def.dmg.min, -card.def.dmg.max);
				else
					fx.life = -MathUtils.random(card.def.dmg.min, card.def.dmg.max);
			}
			if(card.def.turns != null){
				fx.turns = MathUtils.random(card.def.turns.min, card.def.turns.max);
			}
			if(card.def.protection != null){
				fx.protection = MathUtils.random(card.def.protection.min, card.def.protection.max);
			}
			target.effects.add(fx);
		}
		card.turns = card.def.wait;
		
//		for(CharacterBattle character : characters)
//		{
//			character.cards.sort(cardComparator);
//			CardBattle c = character.cards.first();
//			character.turns = c.turns;
//		}
//		characters.sort(characterComparator);
	}
}

package net.mgsx.game.examples.tactics.logic;

import java.util.Comparator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class AIControl implements CharacterControl
{
	private BattleLogic logic;

	static class CharacterScore
	{
		public CharacterScore(CharacterBattle target) 
		{
			this.character = target;
		}

		CharacterBattle character;
		int minLife;
		int maxLife;
		
		float score;
	}
	
	public AIControl(BattleLogic logic) {
		super();
		this.logic = logic;
	}

	@Override
	public void enable(CharacterBattle character) 
	{
		// global AI :
		// take first card (other cards will be played next turn)
		// choose best target :
		// - where fx is maximum
		// - where have a chance to kill (don't use big weapon on mostly dead or weak opponent)
		
		// simple AI :
		// get first card from deck
		CardBattle card = logic.current.cards.first();
		if(card.turns > 0) return;
		
		float minDamage = 0;
		float maxDamage = 0;
		
		int fxTurnsMin = card.def.turns == null ? 1 : card.def.turns.min;
		int fxTurnsMax = card.def.turns == null ? 1 : card.def.turns.max;
		
		if(card.def.dmg != null){
			minDamage = card.def.dmg.min * fxTurnsMin;
			maxDamage = card.def.dmg.max * fxTurnsMax;
		}
		
		TeamBattle team = logic.teamA.characters.contains(character, true) ? logic.teamA : logic.teamB;
		
		Array<CharacterScore> targets = new Array<CharacterScore>();
		
		// get first living opponent
		for(CharacterBattle target : logic.characters){
			if(target.life > 0){
				CharacterScore cs = new CharacterScore(target);
				
				float damageScore = target.life - MathUtils.clamp(target.life - (minDamage + maxDamage) / 2, 0, target.def.life);
				// TODO ...
				cs.score = damageScore / target.life;
				
				// invert score depending on enemy or ally
				if(!team.characters.contains(target, true)){
					cs.score = -cs.score;
				}
				
				targets.add(cs);
			}
		}
		
		targets.sort(new Comparator<CharacterScore>() {
			@Override
			public int compare(CharacterScore o1, CharacterScore o2) {
				return Float.compare(o1.score, o2.score);
			}
		});
		
		if(targets.size > 0){
			CharacterScore cs = targets.first();
			if(cs.score < 0){
				logic.selectAction(card, targets.first().character);
			}
		}
	}

	@Override
	public void disable(CharacterBattle character) {
		// nothing to do
	}
	
	
}

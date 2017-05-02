package net.mgsx.game.examples.tactics.logic;

public class AIControl implements CharacterControl
{
	private BattleLogic logic;

	public AIControl(BattleLogic logic) {
		super();
		this.logic = logic;
	}

	@Override
	public void enable(CharacterBattle character) {
		// TODO logic
		CardBattle card = logic.current.cards.first();
		
		for(CharacterBattle target : logic.teamA.characters){
			if(target.life > 0){
				logic.selectAction(card, target);
				// TODO logic dispatch event : target changed !
				// characterWidgets.get(target).setTarget(true);
				logic.nextTurn();
				return;
			}
		}
	}

	@Override
	public void disable(CharacterBattle character) {
		// TODO nothing
	}
	
	
}

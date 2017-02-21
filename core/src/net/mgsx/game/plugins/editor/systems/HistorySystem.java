package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.commands.CommandHistory;

public class HistorySystem extends EntitySystem
{
	public CommandHistory history= new CommandHistory();;
	
	public void performCommand(Command command) {
		history.add(command);
	}
	
}

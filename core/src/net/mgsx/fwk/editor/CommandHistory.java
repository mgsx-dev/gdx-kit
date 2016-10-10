package net.mgsx.fwk.editor;

import com.badlogic.gdx.utils.Array;

public class CommandHistory 
{
	private Array<Command> history;
	private int head;
	
	public CommandHistory() {
		history = new Array<Command>();
		head = -1;
	}
	
	public void add(Command cmd){
		if(head < history.size-1)
			history.removeRange(head+1, history.size-1);
		history.add(cmd);
		cmd.commit();
		head++;
	}
	
	public boolean undo(){
		if(head >= 0){
			history.get(head--).rollback();
			return true;
		}else{
			return false;
		}
	}
	
	public boolean redo(){
		if(head < history.size-1){
			history.get(++head).commit();
			return true;
		}else{
			return false;
		}
	}

}

package net.mgsx.game.plugins.pd.midi;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.util.MidiEventListener;

public abstract class MidiLooper extends BaseSequencer
{
	protected MidiLoop current = new MidiLoop();
	
	final Array<MidiLoop> queue = new Array<MidiLoop>();
	final Array<MidiLoop> stack = new Array<MidiLoop>();
	
	public MidiLooper(MidiEventListener listener) {
		super(listener);
	}
	
	public void replace(MidiLoop loop){
		queue.clear();
		stack.clear();
		queue.add(loop);
	}
	public void push(MidiLoop loop){
		stack.add(loop);
	}
	public void queue(MidiLoop loop){
		queue.add(loop);
	}

	public MidiLoop current(MidiLoop loop) {
		loop.start = current.start;
		loop.end = current.end;
		return loop;
	}

	public void update(long deltaTick) 
	{
		
		// System.out.println(position);
		long newPosition = position + deltaTick;
		if(current.end != null && newPosition > current.end){
			if(current.count != null && current.count > 0){
				current.count--;
			}
			if(current.count == null || current.count > 0){
				// loop
				process(position, current.end);
				position += deltaTick - (current.end - current.start);
				loopChanged(current);
				process(current.start, position);
				return;
			}
		}
		// check new loop to proceed by priority :
		MidiLoop newLoop = null;
		// stacked
		if(stack.size > 0 && stack.peek() != current){
			// there is a stacked loop which is not the current, let go to stacked.
			newLoop = stack.peek();
		}
		else if(queue.size > 0 && queue.first() != current){
			// there is a queued loop which is not the current, let go to queued.
			newLoop = queue.first();
		}
		
		if(newLoop != null){
			// check when loop should start
			if(newLoop.syncDivision != null){
				long localPosition = position % newLoop.syncDivision;
				long localNewPosition = newPosition % newLoop.syncDivision;
				long syncPos = newLoop.syncPosition  == null ? 0L : newLoop.syncPosition;
				if(localNewPosition > syncPos  || localNewPosition > localPosition){
					// not already
					newLoop = null;
				}
			}
		}
		
		if(newLoop != null){
			// new loop should be spawned now !
			// process passed events
			// process new events
		//	process(position, current.end); // TODO not until loop end but clamp to sync point.
			
			// process(position, newPosition);
			
			if(stack.size > 0 && stack.peek() == current) stack.pop();
			if(queue.size > 0 && queue.first() == current) stack.removeIndex(0);
			
			current = newLoop;
			position = current.start + deltaTick;
				
			loopChanged(current);
			
			process(current.start, position);
			return;
		}
		process(position, newPosition);
		position = newPosition;
	}

	abstract protected void loopChanged(MidiLoop loop);

	abstract protected void process(long tickMin, long tickMax);

	public void setPosition(long deltaTick) {
		// TODO may jump ...
	}

	
}

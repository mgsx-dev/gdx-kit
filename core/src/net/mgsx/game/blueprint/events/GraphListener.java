package net.mgsx.game.blueprint.events;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import net.mgsx.game.blueprint.GraphNode;
import net.mgsx.game.blueprint.Link;
import net.mgsx.game.blueprint.events.GraphEvent.LinkAddedEvent;
import net.mgsx.game.blueprint.events.GraphEvent.LinkRemovedEvent;
import net.mgsx.game.blueprint.events.GraphEvent.NodeMovedEvent;

public class GraphListener implements EventListener
{
	public void link(Link link){}
	public void unlink(Link link){}
	public void moved(GraphNode node){}
	
	@Override
	public boolean handle(Event event) {
		if(event instanceof LinkAddedEvent){
			link(((LinkAddedEvent) event).link);
		}else if(event instanceof LinkRemovedEvent){
			unlink(((LinkRemovedEvent) event).link);
		}else if(event instanceof NodeMovedEvent){
			moved(((NodeMovedEvent) event).node);
		}else{
			return false;
		}
		return true;
	}

}

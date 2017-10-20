package net.mgsx.game.blueprint.events;

import com.badlogic.gdx.scenes.scene2d.Event;

import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;

public class GraphEvent extends Event
{
	public static class LinkAddedEvent extends GraphEvent{
		public Link link;

		public LinkAddedEvent(Link link) {
			super();
			this.link = link;
		}
		
	}
	public static class LinkRemovedEvent extends GraphEvent{
		public Link link;

		public LinkRemovedEvent(Link link) {
			super();
			this.link = link;
		}
		
	}
	public static class NodeMovedEvent extends GraphEvent{
		public GraphNode node;

		public NodeMovedEvent(GraphNode node) {
			super();
			this.node = node;
		}
		
	}
}

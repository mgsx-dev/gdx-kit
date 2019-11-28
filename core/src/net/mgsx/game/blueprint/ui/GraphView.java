package net.mgsx.game.blueprint.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.blueprint.annotations.Node;
import net.mgsx.game.blueprint.events.GraphEvent.LinkAddedEvent;
import net.mgsx.game.blueprint.events.GraphEvent.NodeMovedEvent;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.blueprint.storage.GraphIO;
import net.mgsx.game.core.helpers.ReflectionHelper;

public class GraphView extends WidgetGroup
{
	public static enum LinkLayout{
		DIRECT, ORTHO
	}
	
	public static class GraphViewConfig{
		public final ObjectMap<Class, Color> colorByType = new ObjectMap<Class, Color>();
		public FileHandle file;
		public void setTypeColor(Color color, Class... types) {
			for(Class type : types) colorByType.put(type, color);
		}
		public boolean isKeyDownSave(int key) {
			return isControlsPressed() && key == Input.Keys.S;
		}
		
		public static boolean isControlsPressed(){
			return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
		}
	}
	
	public LinkLayout linkLayout = LinkLayout.ORTHO;
	
	private Array<NodeFactory> nodeFactories = new Array<NodeFactory>();
	
	protected final Graph graph;
	private ShapeRenderer renderer;
	protected final Skin skin;
	
	private Portlet dragPortlet;
	private boolean dragValid;
	
	private SelectBox<String> selector;
	
	protected final GraphViewConfig config;

	public GraphView(final Graph graph, final Skin skin) {
		this(graph, skin, new GraphViewConfig());
	}
	public GraphView(final Graph graph, final Skin skin, final GraphViewConfig config) {
		super();
		this.graph = graph;
		this.skin = skin;
		this.config = config;
		setScale(1f);
		setTransform(true);
		
		setTouchable(Touchable.enabled);
		
		for(GraphNode node : graph.nodes){
			addNode(node);
		}
		
		renderer = new ShapeRenderer();
		
		addListener(new InputListener(){
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				
				boolean addNodePuredataLike = keycode == Input.Keys.NUM_1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
				boolean addNodeBlenderLike = keycode == Input.Keys.A && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
				
				
				if(keycode == Input.Keys.ESCAPE){
					if(selector != null){
						selector.remove();
						selector = null;
					}
				}else if(config.isKeyDownSave(keycode)){
					if(config.file != null){
						new GraphIO().save(graph, config.file);
					}else{
						Gdx.app.error("Blueprint", "Warning: no file configured, skip saving.");
					}
				}
				else if(addNodePuredataLike || addNodeBlenderLike){
					selector = new SelectBox<String>(skin);
					Array<String> items = new Array<String>();
					items.add("");
					final ObjectMap<String, NodeFactory> map = new ObjectMap<String, NodeFactory>();
					for(NodeFactory factory : nodeFactories){
						String key = factory.displayName();
						map.put(key, factory);
						items.add(key);
					}
					selector.setItems(items);
					addActor(selector);
					selector.validate();
					final Vector2 pos = screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
					selector.setBounds(pos.x, pos.y, selector.getPrefWidth(), selector.getPrefHeight());
					selector.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							NodeFactory factory = map.get(selector.getSelected());
							Object object = factory.create();
							selector.remove();
							selector = null;
							addNode(graph.addNode(object, pos.x, pos.y));
						}
					});
					selector.showList();
				}
				return super.keyDown(event, keycode);
			}
		});
		
		addListener(new DragListener(){
			private NodeView dragNode;
			private float px, py;
			private Portlet dropPortlet;

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				getStage().setKeyboardFocus(GraphView.this);
				begin(x, y);
				return super.touchDown(event, x, y, pointer, button);
			}
			
			@Override
			public void dragStart(InputEvent event, float x, float y, int pointer) 
			{
				//begin(x, y);
			}
			private void begin(float x, float y){
				Actor actor = hit(x, y, isTouchable());
				px = x;
				py = y;
				while(actor != null && actor != GraphView.this){
					
					if(actor instanceof NodeView){
						dragNode = (NodeView)actor;
						break;
					}else if(actor.getUserObject() instanceof Portlet){
						dragPortlet = (Portlet)actor.getUserObject();
						break;
					}
					actor = actor.getParent();
				}
			}
			
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) 
			{
				dragValid = true;
				if(dragNode != null){
					float newX = dragNode.getX() + x - px;
					float newY = dragNode.getY() + y - py;
					dragNode.setX(newX);
					dragNode.setY(newY);
					dragNode.node.position.set(newX, newY); // TODO events !
					invalidateHierarchy();
					getStage().cancelTouchFocusExcept(this, GraphView.this);
				}else if(dragPortlet != null){
					if(dropPortlet != null) dropPortlet.actor.setColor(dropPortlet.color);
					dropPortlet = null;
					// TODO check cycles and compatibility ?
					Actor actor = hit(x, y, isTouchable());
					while(actor != null && actor != GraphView.this){
						
						if(actor instanceof NodeView){
//							dragNode = (Node)actor;
//							break;
						}else if(actor.getUserObject() instanceof Portlet){
							
							Portlet portlet = (Portlet)actor.getUserObject();
							if(dragPortlet.outlet != null && portlet.inlet != null ||
									dragPortlet.inlet != null && portlet.outlet != null){
								dropPortlet = portlet;
								boolean outInCompatible = dragPortlet.inlet != null && dropPortlet.accessor.getType().isAssignableFrom(dragPortlet.accessor.getType());
								boolean inOutCompatible = dragPortlet.outlet != null && dragPortlet.accessor.getType().isAssignableFrom(dropPortlet.accessor.getType());
								
								dragValid = outInCompatible || inOutCompatible;
								if(dragValid){
									dropPortlet.actor.getColor().add(Color.DARK_GRAY);
								}else{
									dropPortlet.actor.getColor().mul(.5f);
								}
								break;
							}
							
						}
						actor = actor.getParent();
					}
					getStage().cancelTouchFocusExcept(this, GraphView.this);
				}else{
					
				}
				px = x;
				py = y;
				super.drag(event, x, y, pointer);
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer) {
				end();
				super.dragStop(event, x, y, pointer);
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				end();
				super.touchUp(event, x, y, pointer, button);
			}

			private void end() {
				if(dragNode != null){
					fire(new NodeMovedEvent(dragNode.node));
				}
				dragNode = null;
				if(dropPortlet != null){
					dropPortlet.actor.setColor(dropPortlet.color);
				}
				if(dragPortlet != null && dropPortlet != null){
					
					if(dragValid){
						
						Link link = null;
						if(dragPortlet.outlet != null && dropPortlet.inlet != null){
							link = graph.addLink(dragPortlet, dropPortlet);
						}else if(dragPortlet.inlet != null && dropPortlet.outlet != null){
							link = graph.addLink(dropPortlet, dragPortlet);
						}
						if(link != null){
							fire(new LinkAddedEvent(link));
						}
					}
					
					
				}
				else if(dragPortlet != null){
					
					// TODO create ?
					
				}
				
				dragPortlet = null;
				dropPortlet = null;
			}
		});
	}
	
	private ObjectMap<GraphNode, NodeView> nodeViews = new ObjectMap<GraphNode, NodeView>();
	
	protected NodeView createNodeView(GraphNode node){
		return new NodeView(config, graph, node, skin);
	}
	
	public void load(){
		if(config.file != null && config.file.exists()){
			
			// TODO clear ?
			
			new GraphIO().load(graph, config.file, nodeFactories);
			
			for(GraphNode node : graph.nodes){
				addNode(node);
			}
		}
	}
	
	private void addNode(GraphNode node){
		NodeView nodeView = createNodeView(node);
		nodeView.setX(node.position.x);
		nodeView.setY(node.position.y);
		addActor(nodeView);
		nodeViews.put(node, nodeView);
		
		Color c = Color.WHITE;
		for(Entry<Class, Color> e : config.colorByType){
			if(e.key.isAssignableFrom(node.object.getClass())){
				c = e.value;
				break;
			}
		}
		nodeView.setColor(c);
		node.color.set(c);
	}
	
	Rectangle bounds = new Rectangle();
	
	@Override
	public void layout() 
	{
		bounds.set(0, 0, 0, 0);
		for(Actor actor : getChildren()){
			if(actor instanceof Layout){
				((Layout) actor).layout();
				actor.setSize(((Layout) actor).getPrefWidth(), ((Layout) actor).getPrefHeight());
			}
			bounds.merge(actor.getX(), actor.getY());
			bounds.merge(actor.getX() + actor.getWidth(), actor.getY() + actor.getHeight());
		}
		for(Actor actor : getChildren()){
			actor.setX(actor.getX() - bounds.x);
			actor.setY(actor.getY() - bounds.y);
		}
		bounds.width -= bounds.x;
		bounds.x = 0;
		bounds.height -= bounds.y;
		bounds.y = 0;
		
	}
	
	@Override
	public float getPrefWidth() {
		return bounds.width;
	}
	
	@Override
	public float getPrefHeight() {
		return bounds.height;
	}
	
	private Vector2 a = new Vector2(), b = new Vector2(), m = new Vector2();
	private Rectangle ar = new Rectangle(), br = new Rectangle();
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();

		if (isTransform()) applyTransform(batch, computeTransform());
		
		
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.CYAN);
		for(Link link : graph.links){
			
			renderer.setColor(link.src.color);
			
			link.src.actor.localToAscendantCoordinates(this, a.set(link.getSrcPosition()));
			link.dst.actor.localToAscendantCoordinates(this, b.set(link.getDstPosition()));
			
			
			NodeView actor;
			
			actor = nodeViews.get(link.src.node);
			ar.set(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
			
			actor = nodeViews.get(link.dst.node);
			br.set(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
			
			m.set(a).add(b).scl(.5f);
			
			if(ar.y < br.y){
				m.y = (br.y + ar.y + ar.height) / 2;
			}else{
				m.y = (ar.y + br.y + br.height) / 2;
			}
			
			
			drawLink(renderer, a, b);
			
		}
		
		if(dragPortlet != null){
			
			dragPortlet.actor.localToAscendantCoordinates(this, Link.getCenter(dragPortlet.actor, a));
			getStage().screenToStageCoordinates(b.set(Gdx.input.getX(), Gdx.input.getY()));
			stageToLocalCoordinates(b); //.add(getCullingArea().x, getY());
			
			m.set(a).add(b).scl(.5f);
			
			ar.set(m.x, m.y, 0, 0);
			br.set(m.x, m.y, 0, 0);
			
			if(dragValid){
				renderer.setColor(Color.WHITE);
			}else{
				renderer.setColor(Color.DARK_GRAY);
			}
			
			// renderer.line(a, b);
			if(dragPortlet.outlet != null)
				drawLink(renderer, a, b);
			else
				drawLink(renderer, b, a);
		}
		
		renderer.end();
		
		if (isTransform()) resetTransform(batch);
		
		batch.begin();
	}

	private void drawLink(ShapeRenderer renderer, Vector2 a, Vector2 b) {
		
		float arrowSize = 12;
		float leafMinSize = 32;
		float borderMinSize = 32;
		switch(linkLayout){
		case DIRECT:
			renderer.line(a, b);
			break;
		default:
		case ORTHO:
			
			if(b.x - a.x > leafMinSize * 2){
				renderer.line(a.x, a.y, m.x, a.y);
				renderer.line(m.x, a.y, m.x, b.y);
				renderer.line(m.x, b.y, b.x, b.y);
			}else if(Math.abs(a.x - b.x) < leafMinSize * 2 && Math.abs(a.y - b.y) < leafMinSize * 2){
				renderer.line(a, b);
			}else{
				
				if(br.y + br.height > ar.y && br.y < ar.y){
					// renderer.setColor(Color.RED);
					renderer.line(a.x, a.y, a.x + leafMinSize, a.y);
					renderer.line(a.x + leafMinSize, a.y, a.x + leafMinSize, ar.y + ar.height + borderMinSize);
					renderer.line(a.x + leafMinSize, ar.y + ar.height + borderMinSize, b.x - leafMinSize, ar.y + ar.height + borderMinSize);
					renderer.line(b.x - leafMinSize, ar.y + ar.height + borderMinSize, b.x - leafMinSize, b.y);
					renderer.line(b.x - leafMinSize, b.y, b.x, b.y);
				}else if(ar.y + ar.height > br.y && ar.y < br.y){
					// renderer.setColor(Color.BLUE);
					renderer.line(a.x, a.y, a.x + leafMinSize, a.y);
					renderer.line(a.x + leafMinSize, a.y, a.x + leafMinSize, br.y + br.height + borderMinSize);
					renderer.line(a.x + leafMinSize, br.y + br.height + borderMinSize, b.x - leafMinSize, br.y + br.height + borderMinSize);
					renderer.line(b.x - leafMinSize, br.y + br.height + borderMinSize, b.x - leafMinSize, b.y);
					renderer.line(b.x - leafMinSize, b.y, b.x, b.y);
				}else if(ar.y < br.y){
					// renderer.setColor(Color.BROWN);
					renderer.line(a.x, a.y, a.x + leafMinSize, a.y);
					renderer.line(a.x + leafMinSize, a.y, a.x + leafMinSize, m.y);
					renderer.line(a.x + leafMinSize, m.y, b.x - leafMinSize, m.y);
					renderer.line(b.x - leafMinSize, m.y, b.x - leafMinSize, b.y);
					renderer.line(b.x - leafMinSize, b.y, b.x, b.y);
				}else{
					// renderer.setColor(Color.YELLOW);
					renderer.line(a.x, a.y, a.x + leafMinSize, a.y);
					renderer.line(a.x + leafMinSize, a.y, a.x + leafMinSize, m.y);
					renderer.line(a.x + leafMinSize, m.y, b.x - leafMinSize, m.y);
					renderer.line(b.x - leafMinSize, m.y, b.x - leafMinSize, b.y);
					renderer.line(b.x - leafMinSize, b.y, b.x, b.y);
				}
				
				
			}
			
			break;
		}
		
		renderer.circle(a.x, a.y, 2);
		renderer.circle(b.x, b.y, 6);
//		renderer.line(b.x, b.y, b.x - arrowSize, b.y - arrowSize);
//		renderer.line(b.x, b.y, b.x - arrowSize, b.y + arrowSize);
		
	}
	
	public void addNodeType(Class<?> ...types) {
		for(final Class<?> type : types) addType(type);
	}
	public <T> void addNodeType(Array<Class<? extends T>> types) {
		for(final Class<?> type : types) addType(type);
	}
	
	private void addType(final Class<?> type){
		addNodeFactory(new NodeFactory() {
			@Override
			public String displayName() {
				Node node = type.getAnnotation(Node.class);
				if(node != null && !node.value().isEmpty()){
					return node.value();
				}
				return type.getSimpleName();
			}
			
			@Override
			public Object create() {
				return ReflectionHelper.newInstance(type);
			}
		});
	}
	
	public static interface NodeFactory {
		public Object create();
		public String displayName();
	}
	
	public void addNodeFactory(NodeFactory nodeFactory){
		nodeFactories.add(nodeFactory);
	}

}

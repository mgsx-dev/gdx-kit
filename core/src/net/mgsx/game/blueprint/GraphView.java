package net.mgsx.game.blueprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import net.mgsx.game.core.helpers.ReflectionHelper;

public class GraphView extends WidgetGroup
{
	public static enum LinkLayout{
		DIRECT, ORTHO
	}
	
	public LinkLayout linkLayout = LinkLayout.ORTHO;
	
	private Graph graph;
	private Skin skin;
	private ShapeRenderer renderer;
	
	private Portlet dragPortlet;
	
	private SelectBox<String> selector;

	public GraphView(final Graph graph, final Skin skin) {
		super();
		this.graph = graph;
		this.skin = skin;
		
		setTouchable(Touchable.enabled);
		
		for(NodeView node : graph.nodes){
			addActor(node);
		}
		
		renderer = new ShapeRenderer();
		
		addListener(new InputListener(){
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Input.Keys.ESCAPE){
					if(selector != null){
						selector.remove();
						selector = null;
					}
				}
				else if(keycode == Input.Keys.NUM_1 && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
					selector = new SelectBox<String>(skin);
					Array<String> items = new Array<String>();
					items.add("");
					final ObjectMap<String, Class> map = new ObjectMap<String, Class>();
					for(Class type : graph.types){
						String key = NodeView.getTypeName(type);
						map.put(key, type);
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
							Object object = ReflectionHelper.newInstance(map.get(selector.getSelected()));
							selector.remove();
							selector = null;
							addActor(graph.addNode(object, pos.x, pos.y));
						}
					});
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
				if(dragNode != null){
					dragNode.setX(dragNode.getX() + x - px);
					dragNode.setY(dragNode.getY() + y - py);
					invalidateHierarchy();
					getStage().cancelTouchFocusExcept(this, GraphView.this);
				}else if(dragPortlet != null){
					if(dropPortlet != null) dropPortlet.actor.setColor(Color.WHITE);
					dropPortlet = null;
					// TODO check cycles and compatibility ?
					Actor actor = hit(x, y, isTouchable());
					while(actor != null && actor != GraphView.this){
						
						if(actor instanceof NodeView){
//							dragNode = (Node)actor;
//							break;
						}else if(actor.getUserObject() instanceof Portlet){
							dropPortlet = (Portlet)actor.getUserObject();
							dropPortlet.actor.setColor(Color.RED);
							break;
						}
						actor = actor.getParent();
					}
					getStage().cancelTouchFocusExcept(this, GraphView.this);
					// dragPortlet.
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
				dragNode = null;
				if(dropPortlet != null) dropPortlet.actor.setColor(Color.WHITE);
				if(dragPortlet != null && dropPortlet != null){
					
					graph.addLink(dragPortlet, dropPortlet);
					
				}
				else if(dragPortlet != null){
					
					// TODO create ?
					
				}
				
				dragPortlet = null;
				dropPortlet = null;
			}
		});
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
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();

		if (isTransform()) applyTransform(batch, computeTransform());
		
		
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.setTransformMatrix(batch.getTransformMatrix());
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		for(Link link : graph.links){
//			link.src.actor.getParent().localToParentCoordinates(a.set(link.getSrcPosition()));
//			link.src.actor.getParent().localToParentCoordinates(b.set(link.getDstPosition()));
			
			link.src.actor.localToAscendantCoordinates(this, a.set(link.getSrcPosition()));
			link.dst.actor.localToAscendantCoordinates(this, b.set(link.getDstPosition()));
			
			drawLink(renderer, a, b);
			
		}
		
		if(dragPortlet != null){
			renderer.setColor(Color.RED);
			dragPortlet.actor.localToAscendantCoordinates(this, Link.getCenter(dragPortlet.actor, a));
			getStage().screenToStageCoordinates(b.set(Gdx.input.getX(), Gdx.input.getY()));
			stageToLocalCoordinates(b); //.add(getCullingArea().x, getY());
			
			// renderer.line(a, b);
			
			drawLink(renderer, a, b);
		}
		
		renderer.end();
		
		if (isTransform()) resetTransform(batch);
		
		batch.begin();
	}

	private void drawLink(ShapeRenderer renderer, Vector2 a, Vector2 b) {
		
		switch(linkLayout){
		case DIRECT:
			renderer.line(a, b);
			break;
		default:
		case ORTHO:
			m.set(a).add(b).scl(.5f);
			
			renderer.line(a.x, a.y, m.x, a.y);
			renderer.line(m.x, a.y, m.x, b.y);
			renderer.line(m.x, b.y, b.x, b.y);
			break;
		}
		
		
	}

}

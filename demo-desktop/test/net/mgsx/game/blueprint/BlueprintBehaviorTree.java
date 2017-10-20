package net.mgsx.game.blueprint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.utils.random.FloatDistribution;
import com.badlogic.gdx.ai.utils.random.IntegerDistribution;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.blueprint.events.GraphListener;
import net.mgsx.game.blueprint.model.Graph;
import net.mgsx.game.blueprint.model.Graph.CopyStrategy;
import net.mgsx.game.blueprint.model.GraphNode;
import net.mgsx.game.blueprint.model.Link;
import net.mgsx.game.blueprint.model.Portlet;
import net.mgsx.game.blueprint.ui.DynamicNode;
import net.mgsx.game.blueprint.ui.GraphView;
import net.mgsx.game.blueprint.ui.GraphView.LinkLayout;
import net.mgsx.game.blueprint.ui.GraphView.NodeFactory;
import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.AssetHelper;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.screen.StageScreen;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.btree.ui.FloatDistributionEditor;
import net.mgsx.game.plugins.btree.ui.IntegerDistributionEditor;
import net.mgsx.kit.config.ReflectionClassRegistry;

/**
 * This example shows 2 aspect of Blueprint :
 * - ordering from graphical position
 * - external types
 * 
 * @author mgsx
 *
 */
public class BlueprintBehaviorTree extends GameApplication {

	public static void main(String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry("net.mgsx.game.blueprint", ReflectionClassRegistry.behaviorTree);
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BlueprintBehaviorTree(), config);
	}
	private Graph graph;
	
	@Override
	public void create() 
	{
		super.create();
		
		EntityEditor.defaultTypeEditors.put(FloatDistribution.class, new FloatDistributionEditor());
		EntityEditor.defaultTypeEditors.put(IntegerDistribution.class, new IntegerDistributionEditor());

		
		Skin skin = AssetHelper.loadAssetNow(assets, "uiskin.json", Skin.class);
		StageScreen screen;
		setScreen(screen = new StageScreen(skin));
		
		graph = new Graph(CopyStrategy.NONE);
		
		graph.addNode(new BTNodeBranch(Sequence.class), 100, 100);
		
		
		
		GraphView view = new GraphView(graph, skin);
		
		view.linkLayout = LinkLayout.DIRECT;
		
		// TODO filter in class registry ... ?
		for(final Class<? extends Task> type : ClassRegistry.instance.getSubTypesOf(Task.class)){
			if(Modifier.isAbstract(type.getModifiers())) continue;
			view.addNodeFactory(new NodeFactory() {
				
				@Override
				public String displayName() {
					return type.getSimpleName();
				}
				
				@Override
				public Object create() {
					if(BranchTask.class.isAssignableFrom(type))
						return new BTNodeBranch(type);
					return new BTNodeLeaf(type);
				}
			});
		}
		
		view.addListener(new GraphListener(){

			private Array<GraphNode> orderedChildren = new Array<GraphNode>();
			private Comparator<GraphNode> comparator = new Comparator<GraphNode>() {
				@Override
				public int compare(GraphNode o1, GraphNode o2) {
					return Float.compare(o2.position.y, o1.position.y);
				}
			};
			
			private Array<GraphNode> getOrderedChildren(GraphNode node){
				orderedChildren.clear();
				for(Portlet out : node.outlets){
					if(BTNode.class.isAssignableFrom(out.accessor.getType())){
						for(Link link : out.outLinks){
							orderedChildren.add(link.dst.node);
						}
					}
				}
				orderedChildren.sort(comparator);
				
				return orderedChildren;
			}
			
			@Override
			public void link(Link link) {
				BTNode src = (BTNode)link.src.node.object;
				src.sync(getOrderedChildren(link.src.node));
			}

			@Override
			public void unlink(Link link) {
				BTNode src = (BTNode)link.src.node.object;
				src.sync(getOrderedChildren(link.src.node));
			}

			@Override
			public void moved(GraphNode node) {
				for(Portlet inlet : node.inlets){
					if(inlet.accessor.getType() == BTNode.class){
						for(Link link : inlet.inputLinks){
							((BTNode) link.src.node.object).sync(getOrderedChildren(link.src.node));
						}
					}
				}
			}
			
		});
		
		ScrollPane scroll;
		screen.getStage().addActor(scroll = new ScrollPane(view));
		scroll.setFillParent(true);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	public abstract static class BTNode implements DynamicNode
	{
		@Editable
		public Task task;
		
		private Array<Task> children = new Array<Task>();

		public BTNode(Class<? extends Task> type) {
			super();
			
			// TODO based on @TaskConstraint !! because of decorator ...
			// TODO is it ok with custom task which doen't conform to branch/leaf/decorator ?
			
			if(BranchTask.class.isAssignableFrom(type)){
				Constructor<? extends Task> cons;
				try {
					cons = type.getConstructor(Array.class);
					this.task = cons.newInstance(children);
				} catch (Exception e) {
					throw new GdxRuntimeException(e);
				} 
			}else{
				this.task = ReflectionHelper.newInstance(type);
			}
		}

		public void sync(Array<GraphNode> orderedChildren)
		{
			children.clear();
			for(GraphNode child : orderedChildren){
				children.add(((BTNode)child.object).task);
			}
			task.reset();
		}

		@Override
		public String displayName() {
			return task.getClass().getSimpleName();
		}
		
		@Outlet
		@Editable
		public void debug(){
			print(task, "-");
		}
		
		private static void print(Task task, String prefix){
			System.out.println(prefix + " " + task.toString());
			for(int i=0 ; i<task.getChildCount() ; i++){
				print(task.getChild(i), prefix + " -");
			}
		}
	}
	
	
	@Inlet("parent")
	public static class BTNodeLeaf extends BTNode
	{
		public BTNodeLeaf(Class<? extends Task> type) {
			super(type);
		}
		
	}
	
	@Outlet("children")
	@Inlet("parent")
	public static class BTNodeBranch extends BTNode
	{
		public BTNodeBranch(Class<? extends Task> type) {
			super(type);
		}
		
	}
	
}

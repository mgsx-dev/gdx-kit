package net.mgsx.game.plugins.g3d.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.StringHelper;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.FieldAccessor;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class G3DNodeEditor implements EntityEditorPlugin
{

	@Override
	public Actor createEditor(Entity entity, final Skin skin) 
	{
		// TODO check animations
		final G3DModel model = entity.getComponent(G3DModel.class);
		
//		// XXX patch shader for a node
//		Node node = model.modelInstance.getNode("Cylinder.001", true);
//		// model.modelInstance.userData.
//		if(node != null){
//			final NodePart blockPart = node.parts.get(0);
//			
//	        final TextureAttribute ta = (TextureAttribute)blockPart.material.get(TextureAttribute.Diffuse);
//	        final ColorAttribute ca = (ColorAttribute)blockPart.material.get(ColorAttribute.Emissive);
//	        model.customAnimation = new Animator(){
//	        	@Override
//	        	public void animate(float deltaTime) {
//	        		
//	        		ca.color.set(1,0,0,1);
//	        		((ColorAttribute)blockPart.material.get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
//	        		((ColorAttribute)blockPart.material.get(ColorAttribute.Ambient)).color.set(Color.WHITE);
//	        		ta.offsetU = 0;
//	        		ta.offsetV -= deltaTime * 1; 
//	        		ta.scaleU = 1;
//	        		// XXX speed
//	        	}
//	        };
//		}
		
		final Label animationStats = new Label("", skin);
		animationStats.setColor(Color.GRAY);
		
		// build animation selector
		final SelectBox<String> animationSelector = new SelectBox<String>(skin);
		Array<String> animations = new Array<String>();
		animations.add(""); // default off value
		for(Animation animation : model.modelInstance.animations){
			animations.add(animation.id);
		}
		animationSelector.setItems(animations);
		animationSelector.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String selection = animationSelector.getSelected();
				if(selection.isEmpty()){
					model.animationController.paused = true;
					animationStats.setText("");
				}else{
					model.animationController.allowSameAnimation = true; // XXX at init time !
					model.animationController.paused = false;
					AnimationDesc desc = model.animationController.setAnimation(selection, -1, 1f, null); // loop
					
					int rFrames = 0, tFrames = 0, sFrames = 0;
					for(NodeAnimation na : desc.animation.nodeAnimations){
						if(na.rotation != null) rFrames += na.rotation.size;
						if(na.scaling != null) sFrames += na.scaling.size;
						if(na.translation != null) tFrames += na.translation.size;
					}
					animationStats.setText(desc.animation.duration + "s TRS(" + tFrames + "|" + rFrames + "|" + sFrames + ")");
				}
			}
		});
		
		Table main = new Table(skin);
		
		main.add("Animation");
		main.add(animationSelector);
		main.row();
		main.add(animationStats).colspan(2).row();
		
		
		final Button btBlend = EntityEditor.createBoolean(skin, model.blended);
		
		main.add("Blended");
		main.add(btBlend);
		main.row();
		
		
		btBlend.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(model.blended != btBlend.isChecked()){
					model.blended = btBlend.isChecked();
					model.applyBlending();
				}
			}
		});

		final Button btCulling = EntityEditor.createBoolean(skin, model.culling);
		
		main.add("Culling");
		main.add(btCulling);
		main.row();
		
		
		btCulling.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				model.culling = btCulling.isChecked();
			}
		});
		
		
		// print some stats
		int rFrames = 0, tFrames = 0, sFrames = 0;
		for(Animation a : model.modelInstance.animations){
			for(NodeAnimation na : a.nodeAnimations){
				if(na.rotation != null) rFrames += na.rotation.size;
				if(na.scaling != null) sFrames += na.scaling.size;
				if(na.translation != null) tFrames += na.translation.size;
			}
		}
		int nFrames = rFrames + sFrames + tFrames;
		
		main.add("animations");
		main.add(model.modelInstance.animations.size + " animations, " + nFrames + " TRS(" + tFrames + "|" + rFrames + "|" + sFrames + ")");
		main.row();
		
		
		main.add("materials");
		main.add(String.valueOf(model.modelInstance.materials.size));
		main.row();
		
		main.add("nodes");
		main.add(model.modelInstance.nodes.size + " root nodes, " + totalNodes(model.modelInstance.nodes) + " total nodes");
		main.row();
		
		
		int mpSize = 0;
		for(MeshPart mp : model.modelInstance.model.meshParts){
			mpSize += mp.size;
		}
		main.add("mesh parts");
		main.add(model.modelInstance.model.meshParts.size + " mesh parts, " + mpSize + " indices");
		main.row();
		
		int vCount = 0, iCount = 0, bCount = 0;
		for(Mesh m : model.modelInstance.model.meshes){
			vCount += m.getNumVertices();
			iCount += m.getNumIndices();
			bCount += m.getNumVertices() * m.getVertexAttributes().vertexSize;
		}
		main.add("meshes"); 
		main.add(model.modelInstance.model.meshes.size + " meshes, " + vCount + " vertices, " + iCount + " indices, " + StringHelper.humanBytes(bCount));
		main.row();
		
		final Table nodesTable = new Table(skin);
		main.add("Nodes");
		final TextButton btOpen = new TextButton("Toggle Bones", skin, "toggle");
		main.add(btOpen).row();
		main.add(nodesTable).colspan(2).row();
		
		btOpen.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				nodesTable.reset();
				if(btOpen.isChecked()){
					createNodesEditor(nodesTable, model.modelInstance.nodes, skin);
				}
			}
		});
		
		
		return main;
	}

	private int totalNodes(Iterable<Node> nodes) {
		int i = 0;
		for(Node node : nodes){
			i += totalNodes(node.getChildren()) + 1;
		}
		return i;
	}

	private void createNodesEditor(Table table, Iterable<Node> nodes, Skin skin) {
		for(Node node : nodes){
			
			table.add(node.id).colspan(2).row();
			
			table.add("T");
			EntityEditor.createControl(table, node, new FieldAccessor(node, "translation")); 
			table.row();
			
			table.add("R");
			EntityEditor.createControl(table, node, new FieldAccessor(node, "rotation")); 
			table.row();
			
			table.add("S");
			EntityEditor.createControl(table, node, new FieldAccessor(node, "scale")); 
			table.row();
			
			createNodesEditor(table, node.getChildren(), skin);
		}
	}

}

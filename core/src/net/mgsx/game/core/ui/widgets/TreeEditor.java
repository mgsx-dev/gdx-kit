package net.mgsx.game.core.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

abstract public class TreeEditor<T> extends Table
{
	protected TreeNode<T> root;
	
	public TreeEditor(Skin skin) {
		super(skin);
	}
	
	public void setRoot(TreeNode<T> node){
		root = node;
		rebuildUI();
	}

	private void rebuildUI() {
		clearChildren();
		add(createNode(root));
	}
	
	private Actor createNode(final TreeNode<T> node){
		Actor actor = createRenderer(node);
		
		Table table = new Table(getSkin());
		table.add(actor).expand().fill();
		Table sub = new Table(getSkin());
		table.add(sub);
		if(node.children != null && node.children.size > 0){
			for(TreeNode<T> child : node.children){
				sub.add(createNode(child)).expand().left().row();
			}
		}
		if(maxChildren(node) < node.children.size){
			
			TextButton btAddChild = new TextButton("+", getSkin());
			sub.add(btAddChild).row();
			btAddChild.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// TODO
				}
			});
		}
		
		return table;
	}

	abstract protected int maxChildren(TreeNode<T> node);

	abstract protected Actor createRenderer(TreeNode<T> node);
	abstract protected void commit();
	
	
}

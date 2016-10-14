package net.mgsx.core.tools;

import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;

abstract public class SelectToolBase extends Tool
{
	protected Editor editor;
	public SelectToolBase(Editor editor) {
		super("Select", editor.orthographicCamera);
		this.editor = editor;
	}
	
	protected <T> void handleSelection(T itemSelected, Array<T> selection)
	{
		if(ctrl()){
			if(selection.contains(itemSelected, true)){
				selection.removeValue(itemSelected, true);
				editor.invalidateSelection();
			}else{
				selection.add(itemSelected);
				editor.invalidateSelection();
			}
		}else if(shift()){
			if(!selection.contains(itemSelected, true)){
				selection.add(itemSelected);
				editor.invalidateSelection();
			}
		}else{
			selection.clear();
			selection.add(itemSelected);
			editor.invalidateSelection();
		}
	}

}

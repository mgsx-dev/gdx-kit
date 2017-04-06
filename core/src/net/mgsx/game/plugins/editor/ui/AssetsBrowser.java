package net.mgsx.game.plugins.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.helpers.NativeService.NativeServiceInterface;

/** Pure LibGDX native service skeleton */
public class AssetsBrowser extends Table implements NativeServiceInterface
{
	private final Stage stage;
	private final Skin skin;
	
	private Table fileList;
	
	public AssetsBrowser(Stage stage, Skin skin) 
	{
		this.stage = stage;
		this.skin = skin;
	}
	
	@Override
	public void openSaveDialog(DialogCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openLoadDialog(DialogCallback callback) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

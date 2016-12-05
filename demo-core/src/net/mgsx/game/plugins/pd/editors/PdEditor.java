package net.mgsx.game.plugins.pd.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudio;

public class PdEditor implements GlobalEditorPlugin
{
	private static PdAudio defaultAudio;
	private static PdAudio remoteAudio;
	
	
	@Override
	public Actor createEditor(EditorScreen editor, Skin skin) 
	{
		final Button bt = EntityEditor.createBoolean(skin, false);
		
		bt.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				enableRemote(bt.isChecked());
			}
		});
		
		return bt;
	}


	protected void enableRemote(boolean checked) {
		if(checked){
			if(remoteAudio == null){
				remoteAudio = new PdAudioRemote();
			}
			if(Pd.audio != remoteAudio){
				defaultAudio = Pd.audio;
				if(defaultAudio != null){
					defaultAudio.release();
				}
				remoteAudio.create(new PdConfiguration());// TODO get static config
				Pd.audio = remoteAudio;
			}
		}
		else
		{
			if(Pd.audio == remoteAudio){
				remoteAudio.release();
				if(defaultAudio != null){
					defaultAudio.create(new PdConfiguration()); // TODO get static config
				}
				Pd.audio = defaultAudio;
			}
		}
		
	}

}

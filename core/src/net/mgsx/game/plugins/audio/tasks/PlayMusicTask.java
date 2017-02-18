package net.mgsx.game.plugins.audio.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.audio.Music;

import net.mgsx.game.core.annotations.TaskAsset;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("playMusic")
public class PlayMusicTask extends EntityLeafTask
{
	@TaskAsset(Music.class)
	@TaskAttribute(required=true)
	public String asset;
	
	private Music music;
	
	@Override
	public void start() {
		music = getObject().assets.get(asset, Music.class);
		music.play();
	}
	
	@Override
	public Status execute() {
		return music.isPlaying() ? Status.RUNNING : Status.SUCCEEDED;
	}
	
	@Override
	public void end() {
		music.stop();
		music = null;
	}
}

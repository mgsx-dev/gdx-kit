package net.mgsx.game.examples.video.tools;

import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;

@Editable
public class VideoTool extends Tool
{
	public VideoTool(EditorScreen editor) {
		super("Video", editor);
	}
	
	@Editable
	public float range = 0;
	
	@Editable
	public void load(){
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				try {
					setVideo(file);
				} catch (FileNotFoundException e) {
					throw new GdxRuntimeException(e);
				}
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("mp4") || file.extension().equals("ogg") || file.extension().equals("avi") || file.extension().startsWith("mp") || file.extension().startsWith("w");
			}
			@Override
			public String description() {
				return "Movie file (mp4, ogg, avi, mp*, w*)";
			}
		});
	}

	VideoPlayer player;
	
	@Override
	protected void activate() {
		super.activate();
		player = VideoPlayerCreator.createVideoPlayer();
	}
	
	@Override
	protected void desactivate() {
		player.dispose();
		player = null;
		super.desactivate();
	}
	
	protected void setVideo(FileHandle file) throws FileNotFoundException 
	{
		player.stop();
		player.play(file);
		player.getVideoWidth();
		player.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void render(Batch batch) {
		batch.end();
		
		if(player != null){
			player.getShader().begin();
			player.getShader().setUniformf("u_range", range);
			player.getShader().end();
			player.render();
		}
		
		batch.begin();
	}
	
}

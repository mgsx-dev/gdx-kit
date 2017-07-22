package net.mgsx.game.core.helpers;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.mgsx.game.core.helpers.DownloadHelper.DownloadCallback;

public class RemoteImage extends Image
{
	/**
	 * Replace image texture by a remote texture.
	 * @param image
	 * @param url
	 */
	public static void load(final Image image, final String url)
	{
		DownloadHelper.enqueueJob(new Runnable() {
			@Override
			public void run() {
				try {
					DownloadHelper.downloadTextureDirect(url, new DownloadCallback<Texture>() {
						@Override
						public void onSuccess(Texture texture) {
							image.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
							image.invalidate();
						}

						@Override
						public void onError(Throwable e) {
							Gdx.app.error("Net", "Error downloading URL " + url, e);
						}
					});
				} catch (IOException e) {
					Gdx.app.error("Net", "Error downloading URL " + url, e);
				}
			}
		});
	}
	
	public RemoteImage(String url, final int width, final int height, Scaling scaling, int align) {
		super(new BaseDrawable(){
			@Override
			public float getMinWidth() {
				return width;
			}
			@Override
			public float getMinHeight() {
				return height;
			}
		}, scaling, align);
		load(this, url);
	}
	
	public RemoteImage(String url, final int width, final int height, Scaling scaling) {
		this(url, width, height, scaling, Align.center);
	}
	public RemoteImage(String url, final int width, final int height) {
		this(url, width, height, Scaling.stretch, Align.center);
	}
	public RemoteImage(String url) {
		this(url, 0, 0, Scaling.stretch, Align.center);
	}
}

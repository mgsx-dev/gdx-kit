package net.mgsx.game.core.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.StreamUtils;

public class DownloadHelper {

	public static interface DownloadCallback <T> {
		public void onSuccess(T result);
		public void onError(Throwable e);
	}
	
	private static ThreadPoolExecutor executor;
	
	public static int POOL_MIN = 0;
	public static int POOL_MAX = 4;
	public static int POOL_TIMEOUT_SEC = 4;
	public static int POOL_QUEUE_SIZE = 100;
	
	public synchronized static void enqueueJob(Runnable runnable){
		if(executor == null){
			executor = new ThreadPoolExecutor(POOL_MIN, POOL_MAX, POOL_TIMEOUT_SEC, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(POOL_QUEUE_SIZE, true));
		}
		executor.execute(runnable);
	}
	
	/**
	 * Download bytes from URL.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static byte[] downloadDirect(String url) throws IOException {
		InputStream in = null;
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.setUseCaches(true);
			conn.connect();
			in = conn.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096); // TODO might be too small
			StreamUtils.copyStream(in, out);
			return out.toByteArray(); // TODO make an extra copy which is not required
		} catch (IOException ex) {
			throw ex;
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}

	public static void download(final String url, final DownloadCallback<ByteArray> callback){
		enqueueJob(new Runnable() {
			@Override
			public void run () {
				try {
					// TODO causes copy as well
					callback.onSuccess(ByteArray.with(downloadDirect(url)));
				} catch (IOException e) {
					callback.onError(e);
				}
			}
		});
	}
	
	/**
	 * @param url
	 * @throws IOException
	 */
	public static Pixmap downloadPixmapDirect(final String url) throws IOException
	{
		byte[] bytes = downloadDirect(url);
		return new Pixmap(bytes, 0, bytes.length);
	}
	
	public static void downloadPixmap(final String url, final DownloadCallback<Pixmap> callback) throws IOException
	{
		enqueueJob(new Runnable() {
			@Override
			public void run () {
				try {
					callback.onSuccess(downloadPixmapDirect(url));
				} catch (IOException e) {
					callback.onError(e);
				}
			}
		});
	}

	public static void downloadTextureDirect(final String url, final DownloadCallback<Texture> callback) throws IOException{
		final Pixmap pixmap = downloadPixmapDirect(url);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				callback.onSuccess(new Texture(pixmap));
			}
		});
	}
	public static void downloadTexture(final String url, final DownloadCallback<Texture> callback){
		enqueueJob(new Runnable() {
			@Override
			public void run () {
				try {
					downloadTextureDirect(url, callback);
				} catch (IOException e) {
					callback.onError(e);
				}
			}
		});
	}
}

/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.video;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.video.VideoDecoder.VideoDecoderBuffers;

/**
 * Desktop implementation of the VideoPlayer
 *
 * @author Rob Bogie <rob.bogie@codepoke.net>
 */
public class CustomPlayer implements VideoPlayer {

	 //@formatter:off
	 private static final String vertexShader = "uniform sampler2D u_texture;\n" +
	     "attribute vec4 a_position;    \n" +
		 "attribute vec2 a_texCoord0;\n" +
		 "attribute vec3 a_normal;\n" +
		 "uniform mat4 u_worldView;\n" +
		 "uniform float u_range;\n" +
		 "varying vec2 v_texCoords;" +
		 "varying vec4 v_color;" +
		 "void main()                  \n" +
		 "{                            \n" +
		 "   v_color = texture2D(u_texture, a_texCoord0); \n" +
		 "   v_texCoords = a_texCoord0; \n" +
		 "   gl_Position =  u_worldView * (a_position + vec4(a_normal * u_range * (v_color.r+v_color.g+v_color.b)/3.0, 0.0));  \n" +
		 "}                            \n";
	 private static final String fragmentShader = "varying vec2 v_texCoords;\n" +
		 "uniform sampler2D u_texture;\n" +
		 "varying vec4 v_color;" +
		 "void main()                                  \n" +
		 "{                                            \n" +
		 "  gl_FragColor = texture2D(u_texture, v_texCoords);\n" +
		 "}";

	 //@formatter:on

	 Viewport viewport;
	 Camera cam;

	 VideoDecoder decoder;
	 Pixmap image;
	 Texture texture;
	 RawMusic audio;
	 long startTime = 0;
	 boolean showAlreadyDecodedFrame = false;

	 BufferedInputStream inputStream;
	 ReadableByteChannel fileChannel;

	 boolean paused = false;
	 long timeBeforePause = 0;

	 public ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);

	 Mesh mesh;
	 boolean customMesh = false;

	 int currentVideoWidth, currentVideoHeight;
	 VideoSizeListener sizeListener;
	 CompletionListener completionListener;
	 FileHandle currentFile;

	 boolean playing = false;
	 private int primitiveType = GL20.GL_TRIANGLES;

	 public CustomPlayer () {
		  this(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	 }
	 private static final float SQRT2 = (float)Math.sqrt(2);
	 
	 public CustomPlayer (Viewport viewport) {
		  this.viewport = viewport;

		  mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
		  mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

		  
		  // VERSION attached
		  int w = 48;
		  int h = 48;
		  int vertices_count = (w+1) * (h+1);
		  int indices_count = w*h*2*3;
		  mesh = new Mesh(true, vertices_count, indices_count, VertexAttribute.Position(), VertexAttribute.TexCoords(0), VertexAttribute.Normal());
		  // index
		  short [] indices = new short[indices_count];
		  for(int y=0; y<h ; y++){
			  for(int x=0; x<w ; x++){
				  
				  indices[((y * w) + x) * 6 + 0] = (short)(y * (w+1) + x);
				  indices[((y * w) + x) * 6 + 1] = (short)(y * (w+1) + x + 1);
				  indices[((y * w) + x) * 6 + 2] = (short)((y+1) * (w+1) + x);
				  
				  indices[((y * w) + x) * 6 + 3] = (short)((y+1) * (w+1) + x);
				  indices[((y * w) + x) * 6 + 4] = (short)(y * (w+1) + x + 1);
				  indices[((y * w) + x) * 6 + 5] = (short)((y+1) * (w+1) + (x+1));
			  }
		  }
		  // vertex
		  int stride = 8;
		  float [] vertices = new float [vertices_count * stride];
		  for(int y=0; y<=h ; y++){
			  for(int x=0; x<=w ; x++){
				  vertices[(y * (w+1) + x) * stride + 0] = 640.f * (((float)x / (float)w) - .5f);
				  vertices[(y * (w+1) + x) * stride + 1] = 480.f * (((1 - (float)y / (float)h)) - .5f);
				  vertices[(y * (w+1) + x) * stride + 2] = 0;
				  
				  vertices[(y * (w+1) + x) * stride + 3] = (float)x / (float)w;
				  vertices[(y * (w+1) + x) * stride + 4] = (float)y / (float)h;
				  
				  vertices[(y * (w+1) + x) * stride + 5] = 0;
				  vertices[(y * (w+1) + x) * stride + 6] = 0;
				  vertices[(y * (w+1) + x) * stride + 7] = 0;
			  }
		  }
		  
		  // VERSION detached
		  vertices_count = w*h*4;
		  indices_count = w*h*2*3;
		  mesh = new Mesh(true, vertices_count, indices_count, VertexAttribute.Position(), VertexAttribute.TexCoords(0), VertexAttribute.Normal());
		  // index
		  indices = new short[indices_count];
		  for(int y=0; y<h ; y++){
			  for(int x=0; x<w ; x++){
				  
				  indices[((y * w) + x) * 6 + 0] = (short)((y * w + x) * 4 + 0);
				  indices[((y * w) + x) * 6 + 1] = (short)((y * w + x) * 4 + 1);
				  indices[((y * w) + x) * 6 + 2] = (short)((y * w + x) * 4 + 2);
				  
				  indices[((y * w) + x) * 6 + 3] = (short)((y * w + x) * 4 + 2);
				  indices[((y * w) + x) * 6 + 4] = (short)((y * w + x) * 4 + 1);
				  indices[((y * w) + x) * 6 + 5] = (short)((y * w + x) * 4 + 3);
			  }
		  }
		  // vertex
		  vertices = new float [vertices_count * stride];
		  for(int y=0; y<h ; y++){
			  for(int x=0; x<w ; x++){
				  for(int sy=0;sy<2;sy++){
					  for(int sx=0;sx<2;sx++){
						  int base = (((y * w + x) * 2 + sy) * 2 + sx) * stride;
						  float px = (float)(x+sx) / (float)(w);
						  float py = (float)(y+sy) / (float)(h);
						  vertices[base + 0] = (px - .5f);
						  vertices[base + 1] = ((py) - .5f);
						  vertices[base + 2] = 0;
						  
						  vertices[base + 3] = px;
						  vertices[base + 4] = 1-py;
						  
						  vertices[base + 5] = (sx * 2 - 1) * SQRT2;
						  vertices[base + 6] = (sy * 2 - 1) * SQRT2;
						  vertices[base + 7] = 0;

					  }
					  
				  }
			  }
		  }

		  
		  
		  mesh.setVertices(vertices);
		  mesh.setIndices(indices);

		  
		  cam = viewport.getCamera();
	 }

	 public CustomPlayer (Camera cam, Mesh mesh, int primitiveType) {
		  this.cam = cam;
		  this.mesh = mesh;
		  this.primitiveType = primitiveType;
		  customMesh = true;
	 }

	 @Override public boolean play (FileHandle file) throws FileNotFoundException {
		  if (file == null) {
				return false;
		  }
		  if (!file.exists()) {
				throw new FileNotFoundException("Could not find file: " + file.path());
		  }

		  currentFile = file;

		  if (!FfMpeg.isLoaded()) {
				FfMpeg.loadLibraries();
		  }

		  if (decoder != null) {
				// Do all the cleanup
				stop();
		  }

		  inputStream = file.read(1024 * 1024);
		  fileChannel = Channels.newChannel(inputStream);

		  decoder = new VideoDecoder();
		  VideoDecoderBuffers buffers = null;
		  try {
				buffers = decoder.loadStream(this, "readFileContents");

				if (buffers != null) {
					 ByteBuffer audioBuffer = buffers.getAudioBuffer();
					if(audioBuffer != null) {
						audio = new RawMusic(decoder, audioBuffer, buffers.getAudioChannels(), buffers.getAudioSampleRate());
					}
				} else {
					 return false;
				}
		  } catch (Exception e) {
				e.printStackTrace();
				return false;
		  }

		  if (sizeListener != null) {
				sizeListener.onVideoSize(currentVideoWidth, currentVideoHeight);
		  }

		  image = new Pixmap(buffers.getVideoWidth(), buffers.getVideoHeight(), Format.RGB888);

		  float x = -buffers.getVideoWidth() / 2;
		  float y = -buffers.getVideoHeight() / 2;
		  float width = buffers.getVideoWidth();
		  float height = buffers.getVideoHeight();

		  //@formatter:off
//		  if (!customMesh)
//				mesh.setVertices(
//					new float[] {x, y, 0, 0, 1, x + width, y, 0, 1, 1, x + width, y + height, 0, 1, 0, x, y + height, 0, 0, 0});
		  //@formatter:on

		  if (viewport != null)
				viewport.setWorldSize(1.2f, 1.2f);
		  playing = true;
		  return true;
	 }

	 @Override public void resize (int width, int height) {
		  if (!customMesh) {
				viewport.update(width, height);
		  }
	 }

	 /**
	  * Called by jni to fill in the file buffer.
	  *
	  * @param buffer The buffer that needs to be filled
	  * @return The amount that has been filled into the buffer.
	  */
	 @SuppressWarnings("unused") private int readFileContents (ByteBuffer buffer) {
		  try {
				buffer.rewind();
				return fileChannel.read(buffer);
		  } catch (IOException e) {
				e.printStackTrace();
		  }
		  return 0;
	 }

	 int cnt = 0;
	 
	 @Override public boolean render () {
		  if (decoder != null && !paused) {
				if (startTime == 0) {
					 // Since startTime is 0, this means that we should now display the first frame of the video, and set the
					 // time.
					 startTime = System.currentTimeMillis();
					 if (audio != null) {
						  audio.play();
					 }
				}

				if (cnt <= 0) {
					cnt = 4;
					 ByteBuffer videoData = decoder.nextVideoFrame();
					 if (videoData != null) {

						  ByteBuffer data = image.getPixels();
						  data.rewind();
						  data.put(videoData);
						  data.rewind();
						  if (texture == null) {
								texture = new Texture(image);
						  }else{
							  texture.draw(image, 0, 0);
						  }
					 } else {
						  if (completionListener != null) {
								completionListener.onCompletionListener(currentFile);
						  }
						  playing = false;
						  if(texture != null)
							  renderTexture();
						  return false;
					 }
				}

				
				long currentFrameTimestamp = (long)(decoder.getCurrentFrameTimestamp() * 1000);
				long currentVideoTime = (System.currentTimeMillis() - startTime);
				int difference = (int)(currentFrameTimestamp - currentVideoTime);
				if (difference > 160) {
					 // Difference is more than a frame, draw this one twice
				}
				cnt--;

				if(texture != null)
					renderTexture();

		  }
		  return true;
	 }

	 private void renderTexture () {
		  texture.bind();
		  shader.begin();
		  shader.setUniformMatrix("u_worldView", cam.combined);
		  shader.setUniformi("u_texture", 0);
		  mesh.render(shader, primitiveType);
		  shader.end();
	 }

	 /**
	  * Will return whether the buffer is filled. At the time of writing, the buffer used can store 10 frames of video.
	  * You can find the value in jni/VideoDecoder.h
	  *
	  * @return whether buffer is filled.
	  */
	 @Override public boolean isBuffered () {
		  if (decoder != null) {
				return decoder.isBuffered();
		  }
		  return false;
	 }

	 @Override public void stop () {
		  playing = false;

		  if (audio != null) {
				audio.dispose();
				audio = null;
		  }
		  if (texture != null) {
				texture.dispose();
				texture = null;
		  }
		  if (image != null) {
				image.dispose();
				image = null;
		  }
		  if (decoder != null) {
				decoder.dispose();
				decoder = null;
		  }
		  if (inputStream != null) {
				try {
					 inputStream.close();
				} catch (IOException e) {
					 e.printStackTrace();
				}
				inputStream = null;
		  }

		  startTime = 0;
		  showAlreadyDecodedFrame = false;
	 }

	 @Override public void pause () {
		  if (!paused) {
				paused = true;
				audio.pause();
				timeBeforePause = System.currentTimeMillis() - startTime;
		  }
	 }

	 @Override public void resume () {
		  if (paused) {
				paused = false;
				audio.play();
				startTime = System.currentTimeMillis() - timeBeforePause;
		  }
	 }

	 @Override public void dispose () {
		  stop();

		  if (!customMesh && mesh != null) {
				mesh.dispose();
		  }
	 }

	 @Override public void setOnVideoSizeListener (VideoSizeListener listener) {
		  sizeListener = listener;
	 }

	 @Override public void setOnCompletionListener (CompletionListener listener) {
		  completionListener = listener;
	 }

	 @Override public int getVideoWidth () {
		  return currentVideoWidth;
	 }

	 @Override public int getVideoHeight () {
		  return currentVideoHeight;
	 }

	 @Override public boolean isPlaying () {
		  return playing;
	 }

	@Override
	public ShaderProgram getShader() {
		return shader;
	}
}

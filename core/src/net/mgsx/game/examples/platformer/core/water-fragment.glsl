#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_texture1;
uniform float u_time;
uniform vec2 u_world;
uniform float u_frequency;
uniform float u_rate;

float rand(float t){
	return fract(sin(t * 45674.54 + 234.543));
}

float noise(float value){
	float i = floor(value);
	float f = fract(value);
	return mix(rand(i), rand(i+1.0), f);
}

float noise2(vec2 pos){
	float ix = floor(pos.x);
	float fx = fract(pos.x);
	float iy = floor(pos.y);
	float fy = fract(pos.y);
	float v00 = rand(ix);
	float v10 = rand(ix+1.0);
	float v01 = rand(iy);
	float v11 = rand(iy+1.0);
	
	return mix(mix(v00, v10, fx), mix(v01, v11, fx), fy);
}

void main()
{
	vec4 mask = texture2D(u_texture1, v_texCoords);
	
	vec2 noise = mask.a * (vec2(noise(v_texCoords.x * u_frequency + u_time + u_world.x), noise(v_texCoords.y * u_frequency + u_time + u_world.y)) - vec2(0.5, 0.5)) * u_rate;
	gl_FragColor = v_color * texture2D(u_texture, v_texCoords + noise);
}
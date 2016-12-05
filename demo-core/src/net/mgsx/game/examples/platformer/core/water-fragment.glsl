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
uniform sampler2D u_texture2;
uniform float u_time;
uniform vec2 u_world;
uniform float u_frequency;
uniform float u_rate;

uniform vec2 focusNear;
uniform vec2 focusFar;

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

vec3 xdebug(float value)
{
	vec3 color;
	if(value < 0.0){
		color = vec3(1.0, mod(-value, 1.0), 0.0);
	}
	else{
		color = vec3(0.0, 1.0, mod(value, 1.0));
	}
	return color;
}

void main()
{
	vec4 mask = texture2D(u_texture1, v_texCoords);
	
	vec2 noise = mask.r * (vec2(noise(v_texCoords.x * u_frequency + u_time + u_world.x), noise(v_texCoords.y * u_frequency + u_time + u_world.y)) - vec2(0.5, 0.5)) * u_rate;
	vec4 dcolor = v_color * texture2D(u_texture, v_texCoords + noise);

	// float circle = 2.0 * distance(v_texCoords.xy, vec2(0.5, 0.5));
//	float circle = 2.0 * max(abs(v_texCoords.x - 0.5), abs(v_texCoords.y - 0.5)) + 0.3 * abs(v_texCoords.x - 0.5) * abs(v_texCoords.y - 0.5);

	float circle = abs(v_texCoords.x - 0.5) * 2.0 + abs(v_texCoords.y - 0.5) * 2.0 - 4.0 * abs(v_texCoords.x - 0.5) * abs(v_texCoords.y - 0.5);
	float circle2 = 2.0 * distance(v_texCoords.xy, vec2(0.5, 0.5));

	float depth = mask.b;
	float cocX;
	if(depth > focusFar.y) cocX = 1.0;
	else if(depth > focusFar.x) cocX = smoothstep(focusFar.x, focusFar.y, depth);
	else if(depth < focusNear.y) cocX = 1.0;
	else if(depth < focusNear.x) cocX = smoothstep(focusNear.x, focusNear.y, depth);
	else cocX = 0.0;


	circle = smoothstep(0.97, 1.01, circle);

	dcolor = 1.0 * mix( dcolor, 1.0 * texture2D(u_texture2, v_texCoords), min(cocX + smoothstep(0.8, 1.0, circle2), 1.0)); // * 0.01 + vec4(debug, 1.0);

	vec4 cocColor = mix(dcolor, vec4(0,0,0,1), circle);
#ifdef DEBUG_LIMITS
	vec3 col;
	float nearSize = focusNear.y - focusNear.x;
	float farSize = focusFar.y - focusFar.x;
	if(depth > focusFar.y) col = vec3(0.0, 0.0, 0.0);
	else if(depth > focusFar.x) col = vec3(0.0, 1.0 - (depth - focusFar.x) / farSize, 1.0);
	else if(depth < focusNear.y) col = vec3(0.0, 0.0, 0.0);
	else if(depth < focusNear.x) col = vec3(1.0, 1.0 - (depth - focusNear.x) / nearSize, 0.0);
	else col = vec3(0.0, 1.0, 0.0);
	gl_FragColor = cocColor * 0.001 + vec4(col, 1.0);
#else
	gl_FragColor = cocColor;
#endif
}

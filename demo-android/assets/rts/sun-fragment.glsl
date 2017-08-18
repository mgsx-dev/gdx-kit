#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
varying vec4 v_color;
uniform float uu_time;

void main() {

	vec2 coords = v_texCoords - vec2(0.5, 0.5);
	float angle = atan(coords.y, coords.x);
	float distance = length(coords);
	float time = uu_time;
	float fade = 0.5;
	float dst = distance + 0.5 * (texture2D(u_texture, vec2(fract(3.0 * angle / 6.283 + 0.5 + time), fract(time))).r - 0.5);
	float alpha = 1.0 - smoothstep(0.5 - fade, 0.5 + fade, dst * 2.0);

	vec4 color = texture2D(u_texture, vec2(fract(4.0 * angle / 6.283 - time * 10.0), distance)) * (1.0 - distance + 4.5);

	float rate = smoothstep(0.2, 0.3, distance);

	color = mix(vec4(v_color.rgb, 1.0), vec4(v_color.rgb * color * alpha, alpha), rate);

	gl_FragColor = color;
}

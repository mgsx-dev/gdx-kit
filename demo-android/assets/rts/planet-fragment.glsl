#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
varying vec4 v_color;
uniform vec4 u_color;

void main() {

	float dst = length(v_texCoords - vec2(0.5, 0.5));
	vec3 desert = vec3(0.5, 0.4, 0.0);
	vec3 forest = u_color.rgb; //vec3(0.0, 0.5, 0.0);
	float health = v_color.r;
	float solar = v_color.g;
	float size = v_color.b;
	float units = v_color.a;
	vec3 base = mix(desert, forest, health);

	vec3 ocean_good = vec3(0.0, 0.0, 0.5);
	vec3 ocean_bad = vec3(0.5, 0.5, 0.0);

	vec3 ocean = mix(ocean_bad, ocean_good, health);

    float fade = 0.05;

	float rate = smoothstep(units - fade, units + fade, texture2D(u_texture, v_texCoords).r);

	vec3 glow = forest;

	vec3 color = mix(base, ocean, rate);


	if(dst < 0.4)
		gl_FragColor = vec4(color, 1.0);
	else if(dst < 0.5 && dst > 0.45)
		gl_FragColor = vec4(glow, 1.0);
   // gl_FragColor = vec4(color * (dst > 0.4 ? 0.0 : 1.0), dst > 0.4 ? 0.0 : 1.0) + vec4(glow * 1.0, (dst > 0.5 || dst < 0.45) ? 0.0 : 1.0);
}

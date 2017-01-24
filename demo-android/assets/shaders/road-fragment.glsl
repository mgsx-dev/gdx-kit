#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
varying vec4 v_color;

void main() {

	float rate = v_texCoords.y * 0.8 + 0.2; //1.0 - abs(v_texCoords.y - 0.5) * 1.5 + 0.5; //1.0 - v_texCoords.y * (1.0 - v_texCoords.y) * 4.0;
	rate *= 1.0 - abs(v_texCoords.x - 0.5) * 2.0;
	rate *= rate;
	vec3 color = texture2D(u_texture, v_texCoords);

    gl_FragColor = vec4(color.r * vec3(0.2, 0.6, 1.0), rate * 0.5);
}

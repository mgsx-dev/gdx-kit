#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {

	vec4 c = texture2D(u_texture, v_texCoords);
	c *= (1.0 - c.a) * 8.0;
    gl_FragColor = vec4(c.r, c.g, c.b, 1.0);
}

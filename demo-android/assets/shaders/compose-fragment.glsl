#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_textureOver;

void main() {

	vec4 c1 = texture2D(u_texture, v_texCoords);
	vec4 c2 = texture2D(u_textureOver, v_texCoords);
	vec3 c = c1.rgb * c1.a + c2.rgb * 8.0;
    gl_FragColor = vec4(c, 1.0);
}

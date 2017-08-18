#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_textureOver;

uniform float u_boost;
uniform float u_decimate;

void main() {

	vec4 c1 = texture2D(u_texture, v_texCoords);
	vec4 c2 = texture2D(u_textureOver, v_texCoords);
	c1.rgb = floor(c1.rgb * u_decimate) / u_decimate;
	vec3 c = c1.rgb * c1.a + c2.rgb * u_boost;
    gl_FragColor = vec4(c, 1.0);
}

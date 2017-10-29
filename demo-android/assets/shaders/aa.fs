#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 size;


void main() {
    vec4 sum = vec4(0.0);
    vec2 tc = v_texCoords;

    sum += texture2D(u_texture, vec2(tc.x - size.x, tc.y - size.y));
    sum += texture2D(u_texture, vec2(tc.x + size.x, tc.y - size.y));
    sum += texture2D(u_texture, vec2(tc.x - size.x, tc.y + size.y));
    sum += texture2D(u_texture, vec2(tc.x + size.x, tc.y + size.y));

    gl_FragColor = vec4(sum * 0.25);
}

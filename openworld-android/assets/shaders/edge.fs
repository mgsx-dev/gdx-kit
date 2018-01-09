#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec3 size;
uniform vec4 fill;
uniform vec4 stroke;
uniform float u_boost;
uniform float u_blend;

void main() {
    vec2 tc = v_texCoords;
    
    float px = size.z;
    float dx = px / size.x;
    float dy = px / size.y;
    
    vec4 t1 = texture2D(u_texture, vec2(tc.x, tc.y));
    vec4 t2 = texture2D(u_texture, vec2(tc.x + dx, tc.y + dy));

    vec4 c = t2 - t1;
    float light = (abs(c.r) + abs(c.g) + abs(c.b)) / 3.0;
    
#if defined(HQ)
    vec4 t3 = texture2D(u_texture, vec2(tc.x + dx, tc.y - dy));
    vec4 c2 = t3 - t1;
    float light2 = (abs(c2.r) + abs(c2.g) + abs(c2.b)) / 3.0;
    light = max(light, light2);
#endif
    
    vec4 final = mix(fill, stroke, clamp(light * u_boost, 0.0, 1.0));
    
    gl_FragColor = mix(t1, final, u_blend);
}

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {

    vec3 c = texture2D(u_texture, v_texCoords).rgb;

    float distance = length(v_texCoords);
    float angle = atan(v_texCoords.y, v_texCoords.x) / 3.1415;

    float rayFeq = 8.0;
    float circleFreq = 4.0;
    float size = 0.9;

    float circles = step(fract(pow(distance, 2.0) * circleFreq), size);
    float rays = step(fract(angle * rayFeq), size);

    float grid = 0.5 * 0.5 + (1.0 - circles * rays) * 0.5;

    float alpha = step(distance, 1.0);

    gl_FragColor = vec4(c.r * 0.001 + 0.5, 0.8, grid, alpha * distance * distance * grid * 0.5);
}

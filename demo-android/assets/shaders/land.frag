varying vec3 v_position;
varying vec3 v_normal;

void main() {
    vec3 N = normalize(v_normal);
    float lum = N.y * 0.5;
    gl_FragColor = vec4(lum, lum, lum, 1.0);
}

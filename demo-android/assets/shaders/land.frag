varying vec3 v_position;
varying vec3 v_normal;

#define lum 0.5
void main() {
    
    gl_FragColor = vec4(lum, lum, lum, 1.0);
}

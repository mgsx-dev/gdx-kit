varying vec4 v_position;

uniform samplerCube u_texture;

void main() {
    gl_FragColor = textureCube(u_texture, v_position.xyz);
}

varying vec3 v_position;
varying vec3 v_normal;

void main() {
    vec3 N = normalize(v_normal);
    float lum = pow(N.y, 3.0) * 0.5;
    float fog = clamp(gl_FragCoord.z * gl_FragCoord.w, 0.0, 1.0);

    vec3 color = mix(vec3(1.0, 0.5, 0.0), vec3(lum, lum, lum), 1.0 - pow(1.0 - fog, 10.0));

    gl_FragColor = vec4(color, 1.0);
}

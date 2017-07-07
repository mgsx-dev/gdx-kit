varying vec3 v_position;
varying vec3 v_normal;

uniform vec3 u_sunDirection;
uniform vec4 u_fogColor;


void main() {
	float day = (-u_sunDirection.y + 1.0) * 0.5;

    vec3 N = normalize(v_normal);
    float lum = pow(clamp(-dot(N, u_sunDirection), 0.0, 1.0), 3.0) * 0.5;
    float fog = clamp(gl_FragCoord.z * gl_FragCoord.w, 0.0, 1.0);

    vec3 color = mix(u_fogColor * day, vec3(lum, lum, lum), 1.0 - pow(1.0 - fog, 10.0));

    gl_FragColor = vec4(color, 1.0);
}

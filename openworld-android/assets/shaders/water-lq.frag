varying vec4 v_position;

void main() {

	float d = sin(v_position.x * u_frequency + u_time) * u_amplitude;

	// project view vector on the plane
	vec3 I = v_position.xyz - u_camPos;
	vec3 R = reflect(I, vec3(0.0,1.0,0.0)) + vec3(d, d, d);
	vec3 F = reflect(R, vec3(0.0,1.0,0.0));

    vec3 color = mix(textureCube(u_texture, R).rgb, textureCube(u_texture, F).rgb, u_transparency);

    gl_FragColor = vec4(color, 1);
}

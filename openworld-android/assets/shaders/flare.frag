varying vec2 v_coord;

uniform vec3 u_sunDirection;
uniform vec3 u_camDirection;
uniform vec4 u_color;
varying float v_rate;

#define PI 3.1415
void main() {

//	float dx = cos(PI * v_position.x);
//	float dy = sin(PI * v_position.y);

	float D = clamp(1.0 - length(v_coord), 0.0, 1.0);

	// D = D * (1.0 - D) * D * 40.0;
	D = sin(D) * 4.0 * pow(1.0 - D, 4.0) * 1.0;

	float day_rate = smoothstep(0.0, 1.0, -(u_sunDirection.y - 0.1) / 0.1);

    gl_FragColor = vec4(mix(vec3(1,1,1), u_color.rgb, v_rate * 4.0), v_rate * D * day_rate);
}

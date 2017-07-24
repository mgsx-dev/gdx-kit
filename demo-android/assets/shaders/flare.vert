attribute vec3 a_position;

uniform mat4 u_projModelView;
uniform vec3 u_sunDirection;
uniform vec3 u_camDirection;
uniform float u_ratio;
uniform float u_size;
// uniform vec3 u_camUp;

varying vec2 v_coord;
varying float v_rate;

void main()
{
//	vec3 T = cross(u_sunDirection, u_camDirection);
//	vec3 D = cross(-T, u_sunDirection);

	v_coord = a_position.xy;

	vec3 position = vec3(a_position.x, a_position.y * u_ratio, a_position.z);

	vec3 D = u_sunDirection + u_camDirection; // * dot(u_sunDirection, u_camDirection);


	float rate = -dot(u_sunDirection, u_camDirection);
	float dx = abs(u_sunDirection.x + u_camDirection.x);

	if(rate > 0.0){
		rate = rate;
	}else{
		rate = 0.0;
	}

	v_rate = -dot(u_sunDirection, u_camDirection) - 0.5;

	gl_Position =  u_projModelView * vec4(0,0,0,0.0000001) + vec4(position * u_size * u_size * 0.5 + vec3(D.xy * (u_size - 0.9) * 2.0, 0) / rate
			   , 1.0);
}

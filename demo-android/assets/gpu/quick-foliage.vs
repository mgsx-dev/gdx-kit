#version 330 compatibility

uniform float time;

// Input from vertex attributes
in vec3 a_position;
in float a_weight;

// output for geo or frag
out float v_weight;
out vec2 v_motion;

uniform mat4 u_projTrans;

void main()
{
	float life = fract(time * 0.2 + a_weight);
	float z = (1.0 - pow(1.0 - life, 4.0)) * 1.0;
	v_weight = a_weight;
	float angle = time * 4.2 * a_weight + a_weight;
	v_motion = vec2(cos(angle), sin(angle));
	gl_Position =  u_projTrans * vec4(a_position.x, a_position.y - z * 0.0001, a_position.z, 1.0);
}


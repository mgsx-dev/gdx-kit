#version 330 compatibility

uniform float time;

// Input from vertex attributes
in vec3 a_position;
in vec2 a_weight;

// output for geo or frag
out vec2 v_weight;

uniform mat4 u_projTrans;

void main()
{
	float life = sin(time * 2.0 + a_weight.x) * 2.0;
	v_weight = a_weight;
	gl_Position =  u_projTrans * vec4(a_position.x, a_position.y + life, a_position.z, 1.0);
}


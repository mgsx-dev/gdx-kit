#version 330 compatibility

uniform float time;

// Input from vertex attributes
in vec3 a_position;
in vec3 a_normal;

// output for geo or frag
out vec3 v_normal;


uniform mat4 u_projTrans;

void main()
{
	v_normal = a_normal.xyz;
	gl_Position =  vec4(a_position, 1.0);
}


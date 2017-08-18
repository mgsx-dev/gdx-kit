#version 400

uniform mat4 u_projTrans;
uniform float u_strength;

// Input from vertex attributes
in vec3 a_position;
in vec3 a_normal;

// output for geo or frag
out vec3 normal;



void main()
{
	normal = a_normal;
	gl_Position = vec4(a_position, 1.0);
}


#version 400

uniform mat4 u_projTrans;
uniform float u_height;
uniform float u_time;
uniform float u_frequency;
uniform float u_roadScale;
uniform float u_roadWidth;
uniform float u_slices;

in vec3 a_position;
out vec3 g_normal;

#include snoise3d

float fnoise(vec3 position){
	
	float roads = abs(snoise(position * u_roadScale));
	
	int level = int(roads * u_slices);
	float flevel = float(level) / u_slices; 
	
	if(roads < u_roadWidth)
		return 0.0;
	
	float nz = abs(snoise(position) + snoise(position * 2.0) * 0.5 + snoise(position * 4.0) * 0.25);
	
	return mix(flevel, nz, flevel);
}

void main()
{
	float e = fnoise(vec3(a_position.x* u_frequency, a_position.y * u_frequency, u_time));
	float s = 0.02;
	
	float ex1 = fnoise(vec3((a_position.x - s )* u_frequency, a_position.y * u_frequency, u_time));
	float ex2 = fnoise(vec3((a_position.x + s )* u_frequency, a_position.y * u_frequency, u_time));
	float ey1 = fnoise(vec3(a_position.x* u_frequency, (a_position.y - s ) * u_frequency, u_time));
	float ey2 = fnoise(vec3(a_position.x* u_frequency, (a_position.y + s ) * u_frequency, u_time));
	
	float dx = ex2 - ex1;
	float dy = ey2 - ey1;
	
	vec3 n = normalize(cross(vec3(1,0,dx * u_height), vec3(0,1,dy * u_height)));
	
	g_normal = n;
	gl_Position = vec4(a_position.x, a_position.y, e * u_height, 1.0);
}


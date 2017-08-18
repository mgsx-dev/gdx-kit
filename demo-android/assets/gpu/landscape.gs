#version 400

#ifdef NORMALS_DEBUG

layout (triangles) in;
layout (line_strip, max_vertices = 2) out;

uniform mat4 u_projTrans;

in vec3 g_normal[];
uniform float u_length;
uniform float u_height;
out vec4 f_color;

void main()
{
	vec4 ave = (gl_in[0].gl_Position + gl_in[1].gl_Position + gl_in[2].gl_Position) / 3.0;
	vec3 nor = normalize(g_normal[0] + g_normal[1] + g_normal[2]);
	
	gl_Position = u_projTrans * ave; 
	f_color = vec4(1.0, 1.0, 1.0, 1.0);
	EmitVertex();
	
	f_color = vec4(1.0, 0.0, 0.0, 1.0);
	gl_Position = u_projTrans * (ave + vec4(nor * u_length, 0.0)); 
	EmitVertex();
}

#else


layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 u_projTrans;

in vec3 g_normal[];
uniform float u_length;
uniform float u_height;
out vec3 f_normal;
out vec3 f_coord;

uniform mat3 u_normalMatrix;


void main()
{
	vec4 ave = (gl_in[0].gl_Position + gl_in[1].gl_Position + gl_in[2].gl_Position) / 3.0;
	vec3 nor = normalize(g_normal[0] + g_normal[1] + g_normal[2]);
	
	for(int i=0 ; i<3 ; i++){
		gl_Position = u_projTrans * gl_in[i].gl_Position; 
		f_normal = g_normal[i];
		f_coord = gl_in[i].gl_Position.xyz;
		EmitVertex();
	}
	
}


#endif
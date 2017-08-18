#version 400

uniform mat4 u_projTrans;

uniform float u_strength;

layout (triangles) in;
layout (line_strip, max_vertices = 2) out;

// in from vertex
in vec3 normal[];
in float dist2[];

// out to fragment
out fData
{
    vec4 v_color;
    float dist;
}frag;    


void main()
{
	
	vec4 center = (gl_in[0].gl_Position + gl_in[1].gl_Position + gl_in[2].gl_Position) / 3.0;
	vec3 nm = normalize(normal[0] + normal[1] + normal[2]) / 3.0;
	
	vec4 offset = center + vec4(nm * u_strength, 0.0);
	
	gl_Position = u_projTrans * center; 
	frag.v_color = vec4(0.0, 1.0, 0.0, 1.0);
	EmitVertex();
	
	gl_Position = u_projTrans * offset; 
	frag.v_color = vec4(1.0, 1.0, 1.0, 1.0);
	EmitVertex();
	
	EndPrimitive();
}

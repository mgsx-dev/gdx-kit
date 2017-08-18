#version 400

layout (triangles) in;
layout (line_strip, max_vertices = 4) out;

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
	
	frag.dist = dist2[0];
	gl_Position = gl_in[0].gl_Position; 
	frag.v_color = vec4((normal[0] + 1.0) * 0.5, 1.0);
	EmitVertex();
	
	frag.dist = dist2[1];
	gl_Position = gl_in[1].gl_Position; 
	frag.v_color = vec4((normal[1] + 1.0) * 0.5, 1.0);
	EmitVertex();
	
	frag.dist = dist2[2];
	gl_Position = gl_in[2].gl_Position; 
	frag.v_color = vec4((normal[2] + 1.0) * 0.5, 1.0);
	EmitVertex();
	
	frag.dist = dist2[0];
	gl_Position = gl_in[0].gl_Position; 
	frag.v_color = vec4((normal[0] + 1.0) * 0.5, 1.0);
	EmitVertex();
	
	EndPrimitive();
}

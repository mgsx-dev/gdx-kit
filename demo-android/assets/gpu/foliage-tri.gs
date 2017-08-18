#version 400

uniform mat4 u_projTrans;

uniform float u_time;
uniform float u_strength;
uniform float u_thickness;

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

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
	
	float rnd = sin(center.x * 32.54 + center.y * 546.786 + center.z * 76.987) * 0.5 + 0.5;
	rnd = rnd * 0.4 + 0.6;
	
	float rndAngle = rnd * 3.1415 * 2.0;
	
	vec2 anim = vec2(cos(rndAngle + u_time * rnd), sin(rndAngle + u_time * rnd)) * u_strength * 0.05;
	
	vec4 offset = center + vec4(nm * u_strength * rnd, 0.0) + vec4(anim.x, anim.y, 0.0, 0.0);
	
	vec4 tang = vec4(u_thickness * (1.0 - clamp(dist2[0], 0.0, 0.9)), 0.0, 0.0, 0.0);
	
	frag.dist = dist2[0];
	gl_Position = u_projTrans * center - tang; 
	frag.v_color = vec4(0.0, rnd, 0.0, 1.0);
	EmitVertex();
	
	gl_Position = u_projTrans * center + tang; 
	frag.v_color = vec4(0.0, rnd, 0.0, 1.0);
	EmitVertex();
	
	gl_Position = u_projTrans * offset; 
	frag.v_color = vec4(rnd * 0.5, rnd, rnd * 0.5, 1.0);
	EmitVertex();
	
	EndPrimitive();
}

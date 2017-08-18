#version 330 compatibility

const float PI2 = 6.28318530718;
const int depth = 24;

uniform float time;
uniform vec2 u_viewport;
uniform vec2 u_world;

layout (lines_adjacency) in;
layout (triangle_strip, max_vertices = 48) out;

// in from vertex
in vec2 v_weight[];

// out to fragment
out fData
{
	vec2 v_weight;
	vec4 v_color;
}frag;    

vec4 quat_from_axis_angle(vec3 axis, float angle)
{
	float half_angle = angle/2;
	return vec4(axis * sin(half_angle), cos(half_angle));
}

vec3 rotate_vertex_position(vec3 position, vec3 axis, float angle)
{ 
  vec4 q = quat_from_axis_angle(axis, angle);
  return position + 2.0 * cross(q.xyz, cross(q.xyz, position) + q.w * position);
}

void main()
{
//	vec4 position = gl_in[0].gl_Position;
//	float weight = v_weight[0];
//	
//	float w = 100.0 / u_world.x;
//	float h = w * u_viewport.x / u_viewport.y;
//	float h2 = h * (2.0 + weight * 6.0);
//	vec2 motion = v_motion[0] * w;
	float w = 3.5;
	
	// frag.v_color = vec4(v_weight[1], 0.5, 0.75, 1.0);
	
	// compute start and end tangents
	vec3 tan_s = normalize(gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz);
	vec3 tan_e = normalize(gl_in[3].gl_Position.xyz - gl_in[1].gl_Position.xyz);
	
	vec3 tan_m = normalize(gl_in[2].gl_Position.xyz - gl_in[1].gl_Position.xyz);
	
	vec3 nor_s = normalize(cross(tan_s, tan_m));
	vec3 nor_e = normalize(cross(tan_m, tan_e));
	vec3 p;
	
	for(int i=0 ; i<depth ; i++)
	{
		float t = float(i) / float(depth-1);
		float angle = t * PI2;
		
		p = rotate_vertex_position(nor_s * w, tan_s, angle);
		gl_Position = gl_in[1].gl_Position + vec4(p, 0.0);
		frag.v_weight = vec2(t, v_weight[1].y);
		EmitVertex();
	
		p = rotate_vertex_position(nor_e * w, tan_e, angle);
		gl_Position = gl_in[2].gl_Position + vec4(p, 0.0);
		frag.v_weight = vec2(t, v_weight[2].y);
		EmitVertex();
	}

    EndPrimitive();

}

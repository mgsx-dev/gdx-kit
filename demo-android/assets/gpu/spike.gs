#version 330 compatibility

uniform float time;
uniform vec2 u_viewport;
uniform vec2 u_world;
uniform float u_strength;
uniform float u_thickness;

uniform mat4 u_projTrans;


layout (triangles) in;
layout (triangle_strip, max_vertices = 5) out;

// in from vertex
in vec3 v_normal[];

// out to fragment
out fData
{
    float v_weight;
    vec4 v_color;
    vec2 v_coord;
}frag;    

void main()
{
	float thickness = u_thickness;
	
	// get triangle center
	vec3 a = gl_in[0].gl_Position.xyz; // / gl_in[0].gl_Position.w;
	vec3 b = gl_in[1].gl_Position.xyz; // / gl_in[1].gl_Position.w;
	vec3 c = gl_in[2].gl_Position.xyz; // / gl_in[2].gl_Position.w;
	
	vec3 na = v_normal[0];
	vec3 nb = v_normal[1];
	vec3 nc = v_normal[2];
	
	vec3 ab = b - a;
	vec3 ac = c - a;
	vec3 cn = normalize(cross(ab, ac));
	
	vec3 ave = (a+b+c) / 3.0;
	
	vec3 da = mix(a, ave, thickness);
	vec3 db = mix(b, ave, thickness);
	vec3 dc = mix(c, ave, thickness);
	
	vec3 sm = na+nb+nc;
	float len = length(sm);
	vec3 nm = sm; // / len;
	vec3 m = ave; // + normalize(nm);
	
	vec4 ta = u_projTrans * vec4(da, 1.0);
	vec4 tb = u_projTrans * vec4(db, 1.0);
	vec4 tc = u_projTrans * vec4(dc, 1.0);
	vec4 tm = u_projTrans * vec4(ave + cn * u_strength , 1.0);
	
	frag.v_weight = v_normal[1].x;
	
	frag.v_color = vec4(1.0, 0.5, 0.0, 1.0); gl_Position = ta; EmitVertex();
	frag.v_color = vec4(1.0, 0.5, 0.0, 1.0); gl_Position = tb; EmitVertex();
	frag.v_color = vec4(1.0, 0.0, 0.0, 1.0); gl_Position = tm; EmitVertex();
	frag.v_color = vec4(1.0, 0.5, 0.0, 1.0); gl_Position = tc; EmitVertex();
	frag.v_color = vec4(1.0, 0.5, 0.0, 1.0); gl_Position = ta; EmitVertex();
	
    EndPrimitive();

}

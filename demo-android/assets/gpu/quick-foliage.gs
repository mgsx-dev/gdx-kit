#version 330 compatibility

uniform float time;
uniform vec2 u_viewport;
uniform vec2 u_world;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

// in from vertex
in float v_weight[1];
in vec2 v_motion[1];

// out to fragment
out fData
{
    float v_weight;
    vec4 v_color;
    vec2 v_coord;
}frag;    

void main()
{
	vec4 position = gl_in[0].gl_Position;
	float weight = v_weight[0];
	
	float w = 100.0 / u_world.x;
	float h = w * u_viewport.x / u_viewport.y;
	float h2 = h * (2.0 + weight * 6.0);
	vec2 motion = v_motion[0] * w;
	
	frag.v_weight = weight;
    gl_Position = position + vec4(0.0, -h, 0.0, 0.0);
    frag.v_color = vec4(0.0, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(-1.0, -1.0);
    EmitVertex();

    frag.v_weight = weight;
    gl_Position = position + vec4(w, 0.0, 0.0, 0.0);
    frag.v_color = vec4(0.5, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(1.0, -1.0);
    EmitVertex();

    frag.v_weight = weight;
    gl_Position = position + vec4(-w, 0.0, 0.0, 0.0);
    frag.v_color = vec4(0.5, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(-1.0, 1.0);
    EmitVertex();

    frag.v_weight = weight;
    gl_Position = position + vec4(motion.x, h2, motion.y, 0.0);
    frag.v_color = vec4(1.0, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(1.0, 1.0);
    EmitVertex();

    EndPrimitive();
    

}

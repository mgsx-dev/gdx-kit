#version 330 compatibility

uniform float time;
uniform vec2 u_viewport;
uniform vec2 u_world;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

// in from vertex
in float v_weight[1];

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
	
	float w = weight * 100.0 / u_world.x;
	float h = w * u_viewport.x / u_viewport.y;
	float h2 = h * (1.0 + weight * 9.0);
	
	frag.v_weight = weight;
    gl_Position = position + vec4(-w, -h, 0.0, 0.0);
    frag.v_color = vec4(0.0, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(-1.0, -1.0);
    EmitVertex();

    frag.v_weight = weight;
    gl_Position = position + vec4(w, -h, 0.0, 0.0);
    frag.v_color = vec4(0.5, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(1.0, -1.0);
    EmitVertex();

    frag.v_weight = weight;
    gl_Position = position + vec4(-w, h2, 0.0, 0.0);
    frag.v_color = vec4(0.5, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(-1.0, 1.0);
    EmitVertex();

    frag.v_weight = weight;
    // gl_Position = position + vec4(0.5 + (sin(time) * 0.5 + 0.5) * weight * 4.4, 0.5 + (cos(time) * 0.5 + 0.5) * weight * 4.4, 0.0, 0.0);
    gl_Position = position + vec4(w, h2, 0.0, 0.0);
    frag.v_color = vec4(1.0, 0.0, 0.0, 0.0);
    frag.v_coord = vec2(1.0, 1.0);
    EmitVertex();

    EndPrimitive();
    

}

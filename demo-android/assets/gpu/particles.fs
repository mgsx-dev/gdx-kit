#version 330 compatibility
uniform float time;

in float v_weight;
in vec4 v_color;
in fData
{
	float v_weight;
    vec4 v_color;
    vec2 v_coord;
}frag;


out vec4 color;

void main() 
{
	float dist = clamp(1.0 - length(frag.v_coord), 0.0, 1.0);
	color = vec4(dist, dist * 0.5, 0.1, (1.0 - frag.v_weight) * dist);
}

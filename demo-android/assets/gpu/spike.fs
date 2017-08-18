#version 330 compatibility
uniform float time;

in fData
{
	float v_weight;
    vec4 v_color;
    vec2 v_coord;
}frag;

out vec4 color;

void main() 
{
	color = frag.v_color;
}

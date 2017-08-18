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
	float f = frag.v_color.r;
	color = vec4(vec3(f*f, f, f*f), 1.0);
}

#version 330 compatibility
uniform float time;

in vec2 v_weight;
in vec4 v_color;
in fData
{
	vec2 v_weight;
    vec4 v_color;
}frag;


out vec4 color;

void main() 
{
	color = vec4(vec3(frag.v_weight.x, frag.v_weight.y, 0.0), 1.0);
}

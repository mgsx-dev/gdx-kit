#version 400

in fData
{
    vec4 v_color;
    float dist;
}frag;

out vec4 color;

void main() 
{
	// color = vec4(v_normal, 1.0);
	// color = vec4(frag.dist, 0.0, 0.0, 1.0);
	color = frag.v_color;
}

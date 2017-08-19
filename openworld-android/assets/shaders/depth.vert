attribute vec3 a_position;
attribute vec3 a_normal;
uniform mat4 u_projViewWorldTrans;

varying float v_depth;


void main()
{
	vec4 pos = u_projViewWorldTrans * vec4(a_position, 1.0);
	v_depth = pos.z * 0.5 + 0.5;
	gl_Position = pos;
}

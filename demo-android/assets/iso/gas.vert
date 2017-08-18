attribute vec4 a_position;
attribute vec4 a_normal;

uniform mat4 u_projViewTrans;
varying vec4 v_position;
varying vec3 v_normal;
void main()
{
	v_position = a_position;
	v_normal = a_normal;
	gl_Position = u_projViewTrans * a_position;
}

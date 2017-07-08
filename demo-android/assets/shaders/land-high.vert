attribute vec3 a_position;
attribute vec3 a_normal;
uniform mat4 u_projTrans;
varying vec3 v_position;
varying vec3 v_normal;

void main()
{
	v_normal = a_normal;
	v_position = a_position;
	gl_Position =  u_projTrans * vec4(a_position, 1.0);
}

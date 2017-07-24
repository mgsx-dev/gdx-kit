attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
uniform mat4 u_projTrans;
varying vec3 v_position;
varying vec3 v_normal;
varying vec4 v_color;

void main()
{
	v_color = vec4(1.0, 1.0, 1.0, 1.0); // TODO not really used ...
	v_normal = normalize(a_normal);
	v_position = a_position;
	gl_Position =  u_projTrans * vec4(a_position, 1.0);
}

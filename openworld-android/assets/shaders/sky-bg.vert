attribute vec3 a_position;

uniform mat4 u_projModelView;

varying vec3 v_position;

void main()
{
	v_position = a_position;
	gl_Position =  u_projModelView * vec4(a_position, 1.0);
}

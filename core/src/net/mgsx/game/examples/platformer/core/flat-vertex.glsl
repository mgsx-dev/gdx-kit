attribute vec4 a_position;
uniform mat4 u_projTrans;
varying vec4 v_position;
void main()
{
	// TODO and bones !!!
	v_position = u_projTrans * a_position;
	gl_Position = v_position;
}

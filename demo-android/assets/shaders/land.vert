attribute vec3 a_position;
attribute vec3 a_normal;
uniform mat4 u_projTrans;
varying vec4 v_position;
varying vec3 v_normal;

#if defined(CLIP_PLANE)
uniform mat4 u_worldTrans;
#endif

void main()
{
	v_normal = a_normal;
	vec4 position = vec4(a_position, 1.0);
	#if defined(CLIP_PLANE)
	v_position = u_worldTrans * position;
	#endif
	gl_Position =  u_projTrans * position;
}

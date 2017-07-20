attribute vec3 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
uniform vec3 u_camPosition;
varying vec3 v_position;
varying float v_alpha;

uniform float u_time;

void main()
{
	float seed = a_texCoord0.x;
	v_alpha = (1.0 - a_position.z) * 0.3;

	float time = u_time * 1.1 + seed;
	float ftime = fract(time);
	float itime = (time - ftime) * 0.2;

	float offset = (ftime - 0.5) * 16;
	float size = 100;
	float len = 1;
	float mx = fract(a_position.x + itime + a_texCoord0.x) - 0.5;
	float my = fract(a_position.y + itime + a_texCoord0.y) - 0.5;
	float near = 0.5;
	v_position = vec3(
			mx * (abs(mx)+near) * size,
			(a_position.z - 0.5) * len - offset,
			my * (abs(my)+near) * size);
	gl_Position =  u_projTrans * vec4(v_position + u_camPosition, 1.0);
}

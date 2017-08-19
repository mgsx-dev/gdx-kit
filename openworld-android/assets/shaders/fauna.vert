attribute vec3 a_position;
attribute vec2 a_texCoord0;
varying vec2 v_texCoord0;

uniform mat4 u_projTrans;
uniform vec3 u_camPosition;
varying vec3 v_position;
varying float v_alpha;

uniform float u_time;

void main()
{
	v_texCoord0 = a_texCoord0;
	float expand = 40.0;
	float period = 16.0;
	vec2 pos2d = (a_position.xy - 0.5) * expand + floor(u_camPosition.xz / period) * period;
	vec2 seed2d = pos2d;
//	float lifeA = max(fract2d.x,fract2d.y);
//	float lifeB = min(fract2d.x,fract2d.y);
//	float rateA = lifeA * (1-lifeA) * 4;
//	float rateB = lifeB * (1-lifeB) * 4;
//	v_alpha = rateA * rateB;
	v_alpha = 1.0; //0.1 + fract((seed2d.x + seed2d.y) * 0.1);

	float finX = seed2d.x + sin(seed2d.x + u_time) + sin(seed2d.y + u_time);
	float finY = seed2d.y + sin(seed2d.x + 54.567 + u_time) + sin(seed2d.y + 54.567 + u_time);
	float finZ = (0.2+sin((sin(seed2d.x + 54.567 + u_time) + sin(seed2d.y + 54.567 + u_time)) * 0.1)) * 4.0;

	v_position = vec3(
			finX,
			(1.1 + sin(u_time + seed2d.x + seed2d.y)) * 1.01 + 4.0,
			finY);


	vec4 screen_pos = u_projTrans * vec4(v_position, 1.0);

	v_alpha = smoothstep(0.0,1.0, (screen_pos.z * screen_pos.w - 130.0) / 30.0);

	float size = 1.0 / (4.0 + screen_pos.z);
	gl_Position =  screen_pos + vec4(a_texCoord0.x * size, a_texCoord0.y * size * 0.4, 0, 0);
}

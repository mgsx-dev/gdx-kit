attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
varying vec3 v_position;
varying vec3 v_normal;
varying vec2 v_texCoord;

#if defined(SHADOWS)
uniform mat4 u_shadowMapProjViewTrans;
varying vec3 v_shadowMapUv;
#endif

void main()
{
#if defined(SHADOWS)
	vec4 spos = u_shadowMapProjViewTrans * vec4(a_position, 1.0);
	v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
	v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998); // 998
#endif

	v_texCoord = a_texCoord0;
	v_normal = a_normal;
	v_position = a_position;
	gl_Position =  u_projTrans * vec4(a_position, 1.0);
}

#version 400

uniform float u_texScale;
uniform float u_roughness;

uniform vec3 u_lightDir;
uniform vec4 u_lightColor;
uniform vec3 u_eyePos;

in vec3 f_normal;
in vec3 f_coord;

out vec4 color;

#include snoise3dg

uniform mat3 u_normalMatrix;


void main() 
{
	// color = vec4(v_normal, 1.0);
	// color = vec4(frag.dist, 0.0, 0.0, 1.0);
	float d = gl_FragCoord.z; // clamp((f_coord.z - u_eyePos.z) / 100.0, 0, 1);
	d = pow(1 - d, 2);
	
	// normal perturbation
	vec3 pert;
	float p = abs(snoise(vec3(f_coord.xy, 0) * u_texScale, pert));
	vec3 normal = normalize(f_normal + pert * u_roughness * d);
	
	// light calculation
	float dot1 = dot(u_lightDir, normal);
	vec3 light = vec3(0.1); // ambient
	if(dot1 > 0.0){
		
		vec3 ray = normalize(f_coord - u_eyePos);
		float dot2 = abs(dot(ray, f_normal));
		
		light += u_lightColor.xyz * clamp(dot1 * 0.5 + 0.000001 * 4 * max(0.0, p) * pow(dot2, 10.0), 0.0, 1.0); //  + pow(dot, 30.0)
	}
	
	// float f = abs(snoise(f_coord.xyx * u_texScale));
	
	color = vec4(light, u_eyePos.x);
}

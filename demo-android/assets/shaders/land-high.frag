varying vec3 v_position;
varying vec3 v_normal;

uniform vec3 u_sunDirection;
uniform vec4 u_fogColor;
uniform vec3 u_camDirection;
uniform vec3 u_camPosition;

uniform mat4 u_projTrans;

#if defined(SHADOWS)
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;


float getShadowness(vec2 offset)
{
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);
    float z_map = dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts);
    //return step(v_shadowMapUv.z, z_map);//+(1.0/255.0));
    float delta = 0.005;
    float value = smoothstep(v_shadowMapUv.z-delta, v_shadowMapUv.z+delta, z_map+0.002);//+(1.0/255.0));
    return 0.25 + clamp(value, 0.0 , 0.75);
}

float getShadow()
{
	return (//getShadowness(vec2(0,0)) +
			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif


//
// Description : Array and textureless GLSL 2D simplex noise function.
//      Author : Ian McEwan, Ashima Arts.
//  Maintainer : stegu
//     Lastmod : 20110822 (ijm)
//     License : Copyright (C) 2011 Ashima Arts. All rights reserved.
//               Distributed under the MIT License. See LICENSE file.
//               https://github.com/ashima/webgl-noise
//               https://github.com/stegu/webgl-noise
//

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

float pnoise(vec2 v) {
	float sum = 0.0;
	float amp = 1.0;
	for(int i=0 ; i<3 ; i++) {
		sum += snoise(v) * amp;
		amp *= 0.5;
		v *= 2.0;
	}
	return (sum + 1.0) * 0.5;
}

float ptnoise(vec2 v) {
	float sum = 0.0;
	float amp = 1.0;
	for(int i=0 ; i<3 ; i++) {
		sum += abs(snoise(v)) * amp;
		amp *= 0.5;
		v *= 2.0;
	}
	return sum * 0.5;
}

float ptxnoise(vec2 v) {
	float sum = 1.0;
	float amp = 1.0;
	for(int i=0 ; i<3 ; i++) {
		float value = snoise(v);
		sum += value * value * amp;
		amp *= 0.5;
		v *= 2.0;
	}
	return sum * 0.5;
}

void main() {
	float day = (-u_sunDirection.y + 1.0) * 0.5;

    vec3 N = normalize(v_normal + vec3(0.0,(pnoise(v_position.xz * 20.0)*0.5+0.5) * 0.9,0.0));
    float lum = pow(clamp(-dot(N, u_sunDirection), 0.03, 1.0), 0.5) * 0.5;
    float fog = clamp(gl_FragCoord.z * gl_FragCoord.w, 0.0, 1.0);
    float fogFact = 1.0 - pow(1.0 - fog, 10.0);

    vec3 color = vec3(lum, lum, lum);

    // select materials
    float amb = 0.4;
    float dif = 0.7;
    float grassPattern = snoise(v_position.xz * 100.0)*0.5+0.5;

    vec3 grass = mix(vec3(amb,1,amb), vec3(dif,1,dif), grassPattern);

    vec3 earth = vec3(1.0, 0.8, 0.6) * (-pow(1.0 - ptnoise(v_position.xz * 0.3), 5.0)*0.3+1.0);

    color = mix(grass, earth, clamp(v_position.y * 20.0 * abs(pnoise(v_position.xz * 13.2)) + pnoise(v_position.xz * 0.2) * 12.3 - 8.0, 0.0, 1.0)) * color;

    // add fog
    color = mix(u_fogColor.rgb * day, color, fogFact);

    color = mix(color, vec3(1.0,1.0,1.0), pow(clamp(-dot(N, u_sunDirection) * dot(normalize(v_position - u_camPosition), u_camDirection), 0.0, 1.0), 30.0));

	#if defined(SHADOWS)
    color *= 0.5 + 0.5 * getShadow();
	#endif

    gl_FragColor = vec4(color, 1.0);

}

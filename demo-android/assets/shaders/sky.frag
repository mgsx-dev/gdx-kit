varying vec4 v_position;

uniform vec3 u_sunDirection;
uniform vec4 u_fogColor;
uniform float u_cloudTime;
uniform float u_cloudRate;
uniform float u_cloudDarkness;
uniform vec2 u_cloudDirection;

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
	for(int i=0 ; i<6 ; i++) {
		sum += snoise(v) * amp;
		amp *= 0.5;
		v *= 2.0;
	}
	return (sum + 1.0) * 0.5;
}

void main() {
	float f = v_position.y; // give horizon !
	vec3 color;
	float day = (-u_sunDirection.y + 1.0) * 0.5;
	vec4 cloudColor = vec4(0,0,0,0);
	if(f<0){
		// land
		color = u_fogColor * day;
	} else{
		// sky
		vec3 colorDay = vec3(0.5, 0.7, 1.0); // day sky
		vec3 colorNight = vec3(0.0, 0.0, 0.0); // night sky


		color = mix(colorNight, colorDay, day);

		vec3 dir = normalize(v_position.xyz);

		float top = 1.0;
		float deform = 0.5;
		vec2 dir2 = dir.xz * top / pow(dir.y, deform);

		// add stars
		float stars = abs(snoise(dir2.xy * 10000));
		// float starPattern = (sin(dir.x) + sin(dir.z) + 2) / 4.0
		if(1.0 - stars > 0.99) {
			color = mix(color, vec3(stars * 0.5 + 0.2, stars * 0.5 + 0.5, stars * 0.3 + 0.7), 1.0 - day);
		}


		// float pattern = (sin(dir2.x) + sin(dir2.y) + 2) / 4.0;


		// Clouds
		float pattern = pnoise(dir2 + u_cloudDirection * u_cloudTime);
		float cloudLum = mix(0.1, 0.9, day);


		float darknessRate = snoise(dir2 * 0.1 + vec2(34.4, 62.8)) * 0.5 + 0.5;
		cloudLum *= mix(1.0, 0.1, pow(darknessRate, u_cloudDarkness)); // control rate

		// cloudLum = mix(cloudLum, cloudLum * 0.9, clamp((pattern - 0.9) / 0.1, 0, 1));
		//if(pattern>0.9) cloudLum -= 0.5;
		cloudColor = vec4(cloudLum, cloudLum, cloudLum, pow(clamp(pattern, 0.0, 1.0), u_cloudRate));

		// color = mix(color, cloudColor.rgb, cloudColor.a);
		//color = mix(color, vec3(cloudLum, cloudLum, cloudLum), pow(clamp(pattern, 0.0, 1.0), u_cloudRate));
	}

	// smooth with fog.
	vec3 fogColor = u_fogColor * day * day; // XXX

	color = mix(color, fogColor, pow(1.0 - clamp(abs(f * 0.01), 0.0, 1.0), 2.0));

	// sun
	float sunRate = 0.9;
	if(f>0){
		vec3 sunDir = u_sunDirection;
		vec3 viewDir = normalize(v_position);
		float sunRate = -dot(sunDir, viewDir);
		sunRate = clamp(pow(sunRate * 2.0 - 0.99, 40.0), 0, 1);
		color = mix(color, vec3(1.0, 1.0, 0.99), sunRate);

	}

	color = mix(color, cloudColor.rgb, cloudColor.a);

    gl_FragColor = vec4(color, sunRate * (1.0 - cloudColor.a * (1.0 - cloudColor.r)));
}

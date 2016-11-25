#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec4 v_position;
uniform float waterFactor;

vec3 debug(float value)
{
	vec3 color;
	if(value < 0.0){
		color = vec3(1.0, 1.0 - mod(-value, 1.0), 0.0);
	}
	else{
		color = vec3(0.0, mod(value, 1.0), 1.0);
	}
	return color;
}

vec3 fdebug(float depth, float width)
{
	vec3 color;
	if(depth < width - 1.0){
		color = vec3(1.0, 0.0, 0.0);
	}
	else if(depth > 1.0 - width){
		color = vec3(0.0, 0.0, 1.0);
	}
	else if(depth > width){
		color = vec3(0.0, 1.0, 1.0);
	}
	else if(depth < -width){
		color = vec3(1.0, 1.0, 0.0);
	}
	else{
		color = vec3(0.0, 1.0, 0.0);
	}
	return color;
}

vec3 rdebug(float value, float res)
{
	float v = mod(value / res, 1.0);
	return vec3(v,v,v);
}

vec3 debugstep(float edgeA, float edgeB, float value)
{
	float t = smoothstep(edgeA, edgeB, value);
	if(t < 0.5) return vec3(1.0, t * 2.0, 0.0);
	return vec3(0.0, (1.0 - t) * 2.0, 1.0);
}


void main()
{
	// 3 ways to get depth in range 0 (near) / 1 (far). Last one is frustum independant.
	float depth = (v_position.z - 1.0) / 199.0;
	//	gl_FragColor = vec4(1.0 / (gl_FragCoord.w * 200), 0.0, waterFactor, 1.0);
	//	gl_FragColor = vec4(1.0 - (1.0 - gl_FragCoord.z) / gl_FragCoord.w, 0.0, waterFactor, 1.0);

	// float depth = (1.0 - gl_FragCoord.z) / gl_FragCoord.w;
#ifdef DEBUG_DEPTH
	gl_FragColor = vec4(debugstep(0.0, 1.0, depth), max(waterFactor, 1.0));
#else
	gl_FragColor = vec4(waterFactor, 0.0, depth, 1.0);
#endif

	//
}

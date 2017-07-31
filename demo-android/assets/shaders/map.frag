varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_waterLevel;

#define resample(_v, _s) (float(int((_v) * (_s))) / (_s))

void main() {

	vec4 info = texture2D(u_texture, v_texCoord);
	float height = info.r;
	vec3 color;
	if(height < 0.5){
		// water
		if(height < u_waterLevel)
			color = vec3(0.5,0.6,1.0) * resample(height + 0.5, 32.0);
		// Sand
		else
			color = vec3(0.9, 0.8, 0.5);
	}else{
		float nh = height * 2.0;
		float ns = 0.6 * resample(nh, 32.0);
		// flora
		if(info.g > 0.5)
			color = vec3(ns * 0.9, ns*0.8,ns*0.5);
		// earth
		else
			color = vec3(ns, ns*0.8,ns*0.5);
	}
    gl_FragColor = vec4(color, 1.0);
}

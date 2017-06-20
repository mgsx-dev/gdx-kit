
uniform vec2 u_resolution;
uniform vec3 u_camPosition;
uniform vec3 u_camDirection;
uniform vec3 u_light;

vec4 iso(vec3 p) {
	float s = 0.0;
	float r = 1.2;
	vec3 delta = p - vec3(s,s,s);
	float dist = length(delta);
	float e = dist - r;
	vec3 n = delta / dist;
	return vec4(e, n.x, n.y, n.z);
}

// raymarch
vec4 interesect( in vec3 ro, in vec3 rd )
{
	vec4 res = vec4(-1.0);

    // raymarch
    float tmax = 10;
    float t = 0;
	for( int i=0; i<60; i++ )
	{
        vec3 pos = ro + t*rd;
		vec4 hnor = iso( pos );
        res = vec4(t,hnor.yzw);
        
		if( hnor.x<0.01 ) break;
		t += hnor.x;
        if( t>tmax ) break;
	}

	if( t>tmax ) res = vec4(-1.0);
	return res;
}

void main() {

	vec2 p = (-u_resolution.xy + 2.0*gl_FragCoord.xy) / u_resolution.y;
	
	vec3 ro = u_camPosition;
	vec3 d = u_camDirection;

	// camera matrix	
	vec3  cw = normalize( u_camDirection );
	vec3  cu = normalize( cross(cw,vec3(0.0,1.0,0.0)) );
	vec3  cv = normalize( cross(cu,cw) );
	vec3  rd = normalize( p.x*cu + p.y*cv + 1.7*cw );

	// render
	vec3 col = vec3(0.0);
	vec4 tnor = interesect( ro, rd );
	float t = tnor.x;
	
	if( t>0.0 )
	{
		vec3 normal = tnor.yzw;
		col = vec3(dot(normal, u_light));
	}
	
    gl_FragColor = vec4(col, 1.0);
}

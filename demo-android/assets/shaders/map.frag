varying vec2 v_texCoord;
uniform sampler2D u_texture;

void main() {

	vec4 info = texture2D(u_texture, v_texCoord);
	float height = info.r;
	vec3 color;
	if(height < 0.5){
		color = vec3(0.5,0.6,1.0) * (height + 0.5);
	}else{
		color = vec3(height, height*0.9,height*0.6);
	}
    gl_FragColor = vec4(color, 1.0);
}

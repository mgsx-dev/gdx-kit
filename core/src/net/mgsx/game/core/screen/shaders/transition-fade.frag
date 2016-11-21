#ifdef GL_ES
#define LOWP lowp
precision mediump float; 
#else
#define LOWP
#endif 
varying LOWP vec4 v_color;
varying vec2 v_texCoords; 
uniform sampler2D u_texture; 


uniform float u_time;  
uniform sampler2D u_texture1; 

void main()                                   
{    
	vec4 colA = texture2D(u_texture, v_texCoords);
	vec4 colB = texture2D(u_texture1, v_texCoords);
	gl_FragColor = mix(colA, colB, u_time);
}

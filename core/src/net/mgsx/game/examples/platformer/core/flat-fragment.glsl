#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

void main()
{
	gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
}
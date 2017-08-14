varying vec4 v_position;

void main()
{
#ifdef HD
	gl_FragColor = vec4(vec3(1.0, 0.5, 0.0) * u_luminosity, 1.0);
#else
	gl_FragColor = vec4(1.0, 0.5, 0.0, 1.0);
#endif
}

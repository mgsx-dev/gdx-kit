#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 dir;

// from https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson5

void main() {
    //this will be our RGBA sum
    vec4 sum = vec4(0.0);

    //our original texcoord for this fragment
    vec2 tc = v_texCoords;


    float radius = 1.0;
    float resolution = 1.0;

    //the amount to blur, i.e. how far off center to sample from
    //1.0 -> blur by one pixel
    //2.0 -> blur by two pixels, etc.
    float blur = radius/resolution;

    //the direction of our blur
    //(1.0, 0.0) -> x-axis blur
    //(0.0, 1.0) -> y-axis blur
    float hstep = dir.x;
    float vstep = dir.y;

    //apply blurring, using a 9-tap filter with predefined gaussian weights

    sum += texture2D(u_texture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.25;

    sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.5;

    sum += texture2D(u_texture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.25;

    //discard alpha for our simple demo, multiply by vertex color and return
    gl_FragColor = vec4(sum.rgb, 1.0);
}

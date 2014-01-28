#version 300 es
#ifdef GL_FRAGMENT_PRECISION_HIGH
# define maxfragp highp
#else
# define maxfragp medp
#endif

precision maxfragp float;

smooth in vec2 uv;
out vec4 farbe;

uniform sampler2D textureSampler;

void main(void){
    //farbe = vec4(uv.x, uv.y, 1, 1);
    farbe = texture( textureSampler, uv );
}
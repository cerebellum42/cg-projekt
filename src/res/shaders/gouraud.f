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
    if (uv.x < 0.01 || uv.x > 0.99 || uv.y < 0.01 || uv.y > 0.99) {
        farbe = vec4(0,0,0,0);
    }
    else {
        farbe = texture( textureSampler, uv );
    }
}
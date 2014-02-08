#version 330

layout(location = 0) in vec3 vertexCoord;
layout(location = 1) in vec2 vertexUv;
layout(location = 2) in vec3 vertexNormal;

smooth out vec2 uv;
smooth out vec3 normal;
smooth out vec4 fragPosition;

uniform mat4 m, v, p;

void main(void) {
    vec4 vCoord4 = vec4(vertexCoord, 1);
    uv = vertexUv;
    normal = normalize(mat3(m) * vertexNormal);
    fragPosition = vCoord4;
    gl_Position = p * v * m * vCoord4;
}
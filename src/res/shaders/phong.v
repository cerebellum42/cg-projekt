#version 330

layout(location = 0) in vec3 vertex;
layout(location = 1) in vec2 vertexUv;
layout(location = 2) in vec3 vertexNormal;

smooth out vec2 uv;
smooth out vec3 normal;
smooth out vec4 lightFrom;

uniform mat4 mvp;
uniform vec4 lightPosition;

void main(void) {
    uv = vertexUv;
    normal = vertexNormal;
    lightFrom = lightPosition - vec4(vertex.x, vertex.y, vertex.z, 1);
    gl_Position = mvp * vec4(vertex, 1);
}

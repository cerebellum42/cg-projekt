#version 300 es

precision highp float;

layout(location = 0) in vec4 ecken;
layout(location = 2) in vec2 vertexUv;

smooth out vec2 uv;

uniform mat4 frustMatrix;

void main(void) {
	//colorToFrag=color;
	uv = vertexUv;
	gl_Position=frustMatrix*ecken;
}
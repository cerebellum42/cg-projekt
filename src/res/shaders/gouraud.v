#version 300 es

precision highp float;

layout(location = 1) in vec4 ecken;
layout(location = 2) in vec2 vertexUv;

smooth out vec2 uv;

uniform mat4 frustMatrix;
uniform int time;
uniform float degreeToRadian;

void main(void) {
	uv = vertexUv;
	gl_Position=frustMatrix*ecken + vec4(0,2f * sin(float(time)/5f*degreeToRadian),0,0);
}
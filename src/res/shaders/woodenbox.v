#version 330

precision highp float;

layout(location = 0) in vec4 ecken;
layout(location = 1) in vec2 vertexUv;

smooth out vec2 uv;

uniform mat4 frustMatrix;
uniform int time;
uniform float degreeToRadian;

void main(void) {
	uv = vertexUv;
	gl_Position=frustMatrix*ecken + vec4(0,2.0 * sin(float(time)/5.0*degreeToRadian),0,0);
}
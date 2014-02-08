smooth in vec2 uv;
smooth in vec3 normal;
smooth in vec4 lightFrom;
out vec4 color;

const vec3 baseColor = vec3(1,0,0);
float lightAngle;

void main(void){
    lightAngle = dot(lightFrom.xyz, normal);
    color = vec4(abs(normal.x),abs(normal.y),abs(normal.z),1);
}

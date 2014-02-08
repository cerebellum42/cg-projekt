in vec2 uv;
in vec3 normal;
in vec4 fragPosition;
out vec4 fragColor;

uniform mat4 vInv;

struct lightSource
{
    float intensity;
    vec4 position;
    vec4 diffuse;
    vec4 specular;
    float constantAttenuation, linearAttenuation, quadraticAttenuation;
};

lightSource light0 = lightSource(
    120.0,
    vec4(-20, -20, 20, 1),
    vec4(1.0,1.0,1.0, 1.0),
    vec4(1.0,1.0,1.0, 1.0),
    0.0, 0.7, 0.3
);

vec4 scene_ambient = vec4(0.5, 0.5, 0.5, 1.0);

struct material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

material frontMaterial = material(
    vec4(0.0, 0.0, 1.0, 1.0),
    vec4(0.4, 0.4, 0.9, 1.0),
    vec4(1.0, 1.0, 1.0, 1.0),
    5.0
);

void main() {
    vec3 normalDirection = normalize(normal);
    vec3 viewDirection = normalize(vec3(vInv * vec4(0.0, 0.0, 0.0, 1.0) - fragPosition));
    vec3 lightDirection;
    float attenuation;

    if (0.0 == light0.position.w) // directional light?
    {
        attenuation = 1.0; // no attenuation
        lightDirection = normalize(vec3(light0.position));
    }
    else // point light
    {
        vec3 positionToLightSource = vec3(light0.position - fragPosition);
        float distance = length(positionToLightSource);
        lightDirection = normalize(positionToLightSource);
        attenuation = 1.0 / (light0.constantAttenuation
         + light0.linearAttenuation * distance
         + light0.quadraticAttenuation * distance * distance);
    }

    vec3 ambientLighting = vec3(scene_ambient) * vec3(frontMaterial.ambient);

    vec3 diffuseReflection = light0.intensity * attenuation
        * vec3(light0.diffuse) * vec3(frontMaterial.diffuse)
        * max(0.0, dot(normalDirection, lightDirection));

    vec3 specularReflection;
    if (dot(normalDirection, lightDirection) < 0.0) { // light source on the wrong side?
        specularReflection = vec3(0.0, 0.0, 0.0); // no specular reflection
    }
    else { // light source on the right side
        specularReflection = light0.intensity * attenuation * vec3(light0.specular) * vec3(frontMaterial.specular)
            * pow(max(0.0, dot(reflect(-lightDirection, normalDirection), viewDirection)), frontMaterial.shininess);
    }

    fragColor = vec4(ambientLighting + diffuseReflection + specularReflection, 1.0);
}

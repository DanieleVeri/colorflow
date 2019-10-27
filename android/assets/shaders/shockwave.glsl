#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D sceneTex;
uniform vec2 center;
uniform float time;

varying vec2 v_texCoords;

void main() {
    vec2 l_texCoords = v_texCoords;
    vec3 shockParams = vec3(10.0, 0.8, 0.1);
    float offset = (time- floor(time))/time;
    float currentTime = (time)*(offset);
    float distance = distance(v_texCoords, center);

    if((distance <= (currentTime + shockParams.z)) && (distance >= (currentTime - shockParams.z))) {
        float diff = (distance - currentTime);
        float powDiff = 0.0;
        if(distance>0.05) {
            powDiff = 1.0 - pow(abs(diff*shockParams.x), shockParams.y);
        }
        float diffTime = diff * powDiff;
        vec2 diffUV = normalize(v_texCoords-center);
        //Perform the distortion and reduce the effect over time
        l_texCoords = v_texCoords + ((diffUV * diffTime)/(currentTime * distance * 40.0));
    }
    gl_FragColor = texture2D(sceneTex, l_texCoords);
}

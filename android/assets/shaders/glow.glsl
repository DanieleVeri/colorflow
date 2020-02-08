#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D sceneTex;
uniform float time;
uniform vec2 resolution;

varying vec2 v_texCoords;

void main() {
    vec2 p = v_texCoords-0.5;
    float radius = length(p);
    vec4 color = vec4(1.0-radius, 1.0-radius, 1.0-radius, 1.0);
    gl_FragColor = mix(texture2D(sceneTex, v_texCoords), color, 1.0-time);
}

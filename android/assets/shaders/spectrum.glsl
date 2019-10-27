#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D sceneTex;
uniform float time;
uniform vec2 resolution;

varying vec2 v_texCoords;

void main() {
    float radius = length(v_texCoords-0.5);
    vec4 color = vec4(0.0, radius, 1.0, 1.0);
    gl_FragColor = color;
}

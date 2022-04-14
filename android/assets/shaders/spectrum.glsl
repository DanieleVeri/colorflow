#ifdef GL_ES
precision highp float;
#endif

#define PI 3.14159265359
#define PI2 6.2831852
#define CIRCLES 4.0

uniform sampler2D sceneTex;
uniform vec2 resolution;
uniform float time;
uniform sampler2D iChannel0;
varying vec2 v_texCoords;

void main()
{
    vec2 uv = (v_texCoords - 0.5);
    uv.y /= resolution.x/resolution.y;
    float cS = 0.5;
    float sm = 1.0 / resolution.y * 2.0; // smooth
    float ps = 1.0 / resolution.y * sqrt(resolution.y) * 3.0; // circle thin
    float d = length(uv)*0.5;
    float a = atan(uv.y, uv.x);
    a = a < 0.0 ? PI + (PI - abs(a)) : a;
    float lPos = a /PI2;
    float m = 0.0;
    float partSize = 1.0 / CIRCLES;
    vec3 col;
    for(float i = CIRCLES; i > 1.0; i -= 1.0) {
        float ilPos = fract(lPos + i*0.1 + time * 0.1);
        float cPos = partSize * i + ilPos * partSize;
        float invPos = partSize * (i + 1.0) - ilPos * partSize;
        float mP0 = texture2D(iChannel0, vec2(partSize * i, 0.0)).x;
        float mP = texture2D(iChannel0, vec2(cPos, 0.0)).x;
        float mPInv = texture2D(iChannel0, vec2(invPos, 0.0)).x;
        mP = (mP + mPInv) / 2.0;
        float rDiff = i*(1.0 / CIRCLES * 0.35);
        float r = mP * (1.0 / CIRCLES * 3.0) - rDiff;
        float subm = smoothstep(cS - ps + r, cS - ps + sm + r, d) * smoothstep(cS + r, cS - sm + r, d);
        if (subm > 0.0) {
            float v = i / CIRCLES * 0.5 + time * 0.05 + mP0 * 0.84;
            col = clamp(abs(fract(v + vec4(3, 2, 1, 0) / 3.0) * 6.0 - 3.0) - 1.0 , 0.0, 1.0).rgb;
        }
        m += subm;
    }
    m = clamp(m, 0.0, 1.0);
    float r = (sin(time * 0.5) * 0.5 + 0.5);
    float b = (cos(time * 0.5) * 0.5 + 0.5);
    vec3 backCol = vec3(r, 0.0, b) * length(uv * 0.75) * 0.5;
    col = mix(backCol, col, m);
    gl_FragColor = vec4(col, 1.0);
}
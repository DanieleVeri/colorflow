#ifdef GL_ES
precision highp float;
#endif

#define PI 3.14159265359
#define PI2 6.2831852

uniform sampler2D sceneTex;
uniform vec2 resolution;
uniform float time;
uniform sampler2D iChannel0;
varying vec2 v_texCoords;

vec3 hsv(in float h, in float s, in float v)
{
    return mix(vec3(1.0), clamp((abs(fract(h + vec3(3, 2, 1) / 3.0) * 6.0 - 3.0) - 1.0), 0.0 , 1.0), s) * v;
}

vec3 formula(in vec2 p, in vec2 c)
{
    const float n = 2.0;
    const int iters = 2;

    float time = time*0.1;
    vec3 col = vec3(0);
    float t = 1.0;
    float dpp = dot(p, p);
    float lp = sqrt(dpp);
    float r = smoothstep(0.0, 0.2, lp);

    for (int i = 0; i < iters; i++) {
        // The transformation
        p = abs(mod(p/dpp + c, n) - n/2.0);

        dpp = dot(p, p);
        lp = sqrt(dpp);

        //Shade the lines of symmetry black
        #if 0
        // Get constant width lines with fwidth()
        float nd = fwidth(dpp);
        float md = fwidth(lp);
        t *= smoothstep(0.0, 0.5, abs((n/2.0-p.x)/nd*n))
        * smoothstep(0.0, 0.5, abs((n/2.0-p.y)/nd*n))
        * smoothstep(0.0, 0.5, abs(p.x/md))
        * smoothstep(0.0, 0.5, abs(p.y/md));
        #else
        // Variable width lines
        t *= smoothstep(0.0, 0.01, abs(n/2.0-p.x)*lp)
        * smoothstep(0.0, 0.01, abs(n/2.0-p.y)*lp)
        * smoothstep(0.0, 0.01, abs(p.x)*2.0)
        * smoothstep(0.0, 0.01, abs(p.y)*2.0);
        #endif

        // Fade out the high density areas, they just look like noise
        r *= smoothstep(0.0, 0.2, lp);

        // Add to colour using hsv
        col += hsv(1.0 - max(p.x, p.y) + t*2.0 + time, 2.0-lp+t, r);
        col *=2.0;

    }

    return (-cos(col/4.0)*0.5 + 0.5)*(t);
}

void main() {
    vec2 p = -1.0 + 2.0 * v_texCoords.xy;
    p.x *= resolution.x / resolution.y;
    p *= 2.0;
    const vec2 e = vec2(0.06545465634, -0.05346356485);
    vec2 c = time*e;
    vec3 col = vec3(0.0);
    float blursamples = 1.0;
    float sbs = sqrt(blursamples);
    float mbluramount = 1.0/resolution.x/length(e)/blursamples*2.0*texture2D(iChannel0, vec2(0.2, 0.0)).x;
    float aabluramount = 1.0/resolution.x/sbs*4.0;
    for (float b = 0.0; b < blursamples; b++) {
        col += formula(
        p + vec2(mod(b, sbs)*aabluramount, b/sbs*aabluramount),
        c + e*mbluramount*b);
    }
    col /= blursamples;
    gl_FragColor = vec4(col, 1.0);
}
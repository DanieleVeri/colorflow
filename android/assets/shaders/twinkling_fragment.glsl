#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D sceneTex;
uniform float time;
uniform vec2 resolution;

varying vec2 v_texCoords;

void main() {
	vec3 c;
	float l,z=time;
	for(int i=0; i<3; i++) {
		vec2 uv, p = v_texCoords;
		uv = p;
		p -= .5;
		p.x *= resolution.x/resolution.y;
		z += .07;
		l = length(p);
		uv += p/l*(sin(z)+1.)*abs(sin(l*9.-z*2.));
		c[i] = .01/length(abs(mod(uv,1.)-.5));
	}
	gl_FragColor = mix(texture2D(sceneTex, v_texCoords), vec4(c/l,1.0), (1.0-time));
}

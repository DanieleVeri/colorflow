#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D sceneTex;
uniform vec2 center;
uniform float time;
uniform vec2 resolution;

varying vec2 v_texCoords;

void main()
{

/********************************************** SHOCKWAVE
	// get pixel coordinates
	vec2 l_texCoords = v_texCoords;

	vec3 shockParams = vec3(10.0, 0.8, 0.05);

    float offset = (time- floor(time))/time;
	float currentTime = (time)*(offset);

	//get distance from center
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
*/


/************************************************* GLOW (NOT OK)
	float distance = clamp(length(center-v_texCoords/resolution), 0.0, 1.0);

    gl_FragColor = mix(texture2D(sceneTex, v_texCoords), vec4(1.0,1.0,1.0,distance), (1.0-time));

*/

/*************************************************** LU BONE
*/
	vec3 c;
	float l,z=time;
	for(int i=0;i<3;i++) {
		vec2 uv,p=v_texCoords;
		uv=p;
		p-=.5;
		p.x*=resolution.x/resolution.y;
		z+=.07;
		l=length(p);
		uv+=p/l*(sin(z)+1.)*abs(sin(l*9.-z*2.));
		c[i]=.01/length(abs(mod(uv,1.)-.5));
	}
	gl_FragColor=mix(texture2D(sceneTex, v_texCoords), vec4(c/l,1.0), (1.0-time));

}

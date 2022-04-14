# COLORFLOW
LibGDX Android game implemented in Kotlin, inspired to a music visualizer.

Before gradle build, run in sequence:
 - `android/jni/create_toolchains.sh`
 - `android/jni/build_ffmpeg.sh`
 - `android/jni/build_aubio.sh`

in order to compile *aubio* that is an audio analysis library used to detect beats and spectrum in the tracks.

LibGDX dev tools: https://libgdx.com/dev/tools/
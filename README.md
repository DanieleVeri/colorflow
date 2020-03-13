# COLORFLOW
LibGDX Android game implemented in Kotlin, inspired to a music visualizer.

Before gradle build, run in sequence:
 - `android/jni/create_toolchains.sh`
 - `android/jni/build_ffmpeg.sh`
 - `android/jni/build_aubio.sh`

in order to compile *aubio* that is an audio analysis library used to detect beats and spectrum in the tracks.

### TODO
### functional
- [S] Scale assets
- [S] Ring -> shader
- [S] Dot arrival on beat
- [S] Asset design
- [S] Score multiplier
- [S] More ads
- [B] Soundcloud api
- [B] Download and analysis while showing ads
- [C] Track analysis using `oneset`, `pitch`, `note` detection
- [C] Share score for reward
- [C] Slot machine (watch ad for a run)
- [D] Chinese version (360 ads?)

### not-functional
- [S] Db upgrade
- [S] Cache beat detection result (in sqlite)
- [S] Basic telemetry
- [C] Improve beat detection (C implementation of the old algorithm)
- [D] Update build scripts to NDK r20

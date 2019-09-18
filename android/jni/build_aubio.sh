#!/bin/bash

[ -z $PLATFORM ] && PLATFORM=android-21
NDK_TOOLCHAINS=$PWD/toolchains

[ -d aubio ] || git clone https://git.aubio.org/aubio/aubio/ 
cd aubio
[ -e ./waf ] || scripts/get_waf.sh
cd -

function build {
    DESTDIR="$PWD/aubio/out/$2"
    [ -d $DESTDIR ] && rm -rf $DESTDIR

    export PKG_CONFIG_PATH="$PWD/ffmpeg/out/$2/lib/pkgconfig"

    CURRENT_TOOLCHAIN=$NDK_TOOLCHAINS/$PLATFORM-$1
    CC=`ls $CURRENT_TOOLCHAIN/bin/*-linux-android*-gcc`

    cd aubio
    CFLAGS="-Os" CC=$CC \
        ./waf distclean configure build install \
        --destdir=$DESTDIR \
        --verbose \
        --with-target-platform=android \
        --enable-avcodec \
        --disable-samplerate \
        --disable-jack \
        --disable-wavread --disable-wavwrite \
        --disable-sndfile \
        --disable-docs
    cd -
}

build arm64 arm64-v8a
build arm armeabi-v7a
build x86 x86

rm -rf "$PWD/musalyzer/aubio/"
mv "$PWD/aubio/out/armeabi-v7a/usr/local/include/aubio" "$PWD/musalyzer/aubio/"
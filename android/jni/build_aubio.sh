#!/bin/bash

[ -z $PLATFORM ] && PLATFORM=android-21

NDK_PATH="/home/dan/Android/Sdk/ndk-bundle"
NDK_TOOLCHAINS=$PWD/toolchains

[ -d aubio ] || git clone https://git.aubio.org/aubio/aubio/ 
cd aubio
[ -e ./waf ] || scripts/get_waf.sh
cd -

function build {
    TMPDIR=/tmp/aubio_build/$1
    [ -d $TMPDIR ] && rm -rf $TMPDIR
    DESTDIR=$PWD/musalyzer/aubio/prebuilt/$2
    [ -d $DESTDIR ] && rm -rf $DESTDIR

    export PKG_CONFIG_PATH="$PWD/libav/out/$2/lib/pkgconfig"

    CURRENT_TOOLCHAIN=$NDK_TOOLCHAINS/$PLATFORM-$1
    CC=`ls $CURRENT_TOOLCHAIN/bin/*-linux-android*-gcc`

    cd aubio
    CFLAGS="-Os" CC=$CC \
        ./waf distclean configure build install \
        --destdir=$TMPDIR \
        --verbose \
        --with-target-platform=android \
        --enable-avcodec \
        --disable-samplerate \
        --disable-jack \
        --disable-wavread --disable-wavwrite \
        --disable-sndfile \
        --disable-docs
    cd -

    mkdir -p $DESTDIR
    mv $TMPDIR/usr/local/lib/libaubio.a $DESTDIR/libaubio.a
}

./create_toolchains.sh

build arm64 arm64-v8a
build arm armeabi-v7a
# build x86_64
# build x86
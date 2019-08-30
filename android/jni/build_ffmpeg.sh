#!/bin/bash

[ -z $PLATFORM ] && PLATFORM=android-21
NDK_TOOLCHAINS=$PWD/toolchains

./create_toolchains.sh

[ -d ffmpeg ] || git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg

function build {
    cd ffmpeg
    SYSROOT=$NDK_TOOLCHAINS/$PLATFORM-$1/sysroot
	DEST="$PWD/out/$2"
	mkdir -p $DEST

	FLAGS="--sysroot=$SYSROOT \
        --cross-prefix=$NDK_TOOLCHAINS/$PLATFORM-$1/bin/*-linux-android* \
        --prefix=$DEST

        --target-os=android \
        --arch=$1 \
        --enable-jni \
        --disable-zlib \
        --enable-cross-compile \

        --disable-shared \
        --disable-symver \
        --enable-small \
        --enable-static \
        --disable-linux-perf \
        --enable-pic \

        --disable-doc \
        --disable-muxers \
        --disable-encoders \
        --disable-decoders \
        --disable-demuxers \
        --disable-filters \
        --disable-parsers \
        --disable-debug \
        --disable-network \

        --enable-demuxer=flac,mp3,ogg \
        --enable-parser=flac,mpegaudio \
        --enable-decoder=flac,mp3"

    EXTRA_CFLAGS="-march=$3 -mfloat-abi=softfp -O2"

	./configure $FLAGS --extra-cflags="$EXTRA_CFLAGS" | tee $DEST/configuration.txt

	make clean
	make -j4 || exit 1
	make install || exit 1
    cd -
}

build arm64 arm64-v8a armv8-a
build arm armeabi-v7a armv7-a
# build x86_64
# build x86

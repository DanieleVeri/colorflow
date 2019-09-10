#!/bin/bash
####################### DON'T USE! USE `build_ffmpeg.sh` INSTEAD

[ -z $PLATFORM ] && PLATFORM=android-21
NDK_TOOLCHAINS=$PWD/toolchains

./create_toolchains.sh

[ -d libav ] || git clone git://git.libav.org/libav.git

function build {
    cd libav
    SYSROOT=$NDK_TOOLCHAINS/$PLATFORM-$1/sysroot

    ECFLAGS="-mfloat-abi=softfp"
    ELDFLAGS="-Wl"
    ARCH_SPECIFIC="--arch=$1 --cross-prefix=$NDK_TOOLCHAINS/$PLATFORM-$1/bin/*-linux-android*"

    ./configure ${ARCH_SPECIFIC} \
        --prefix="$PWD/out/$2" \
        --target-os=linux \
        --sysroot="$SYSROOT" \
        --extra-cflags="$ECFLAGS" \
        --enable-cross-compile \
        --extra-ldflags="$ELDFLAGS" \
        --enable-static \
        --enable-pic \
        --disable-network \
        --disable-symver || exit 1

    make clean
    make -j8
    make install
    cd -
}

build arm64 arm64-v8a
build arm armeabi-v7a
# build x86_64
# build x86

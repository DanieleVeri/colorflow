#!/bin/bash

# NOTE: No needed if ndk version >= r19
# NDKr18 @ https://dl.google.com/android/repository/android-ndk-r18b-linux-x86_64.zip

[ -z $PLATFORM ] && PLATFORM=android-21

NDK_PATH="/home/dan/Android/Sdk/ndk/18.1.5063045"
NDK_TOOLCHAINS=$PWD/toolchains

mkdir -p toolchains

function create {
    CURRENT_TOOLCHAIN=$NDK_TOOLCHAINS/$PLATFORM-$1

    [ -d $CURRENT_TOOLCHAIN ] || \
    $NDK_PATH/build/tools/make-standalone-toolchain.sh \
        --platform=$PLATFORM --arch=$1 \
        --install-dir=$CURRENT_TOOLCHAIN
}

create arm64
create arm
create x86
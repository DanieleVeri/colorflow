cmake_minimum_required(VERSION 3.0.0)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/aubio/)

find_library(log-lib log)

add_library(libaubio STATIC IMPORTED)
set_target_properties(libaubio PROPERTIES 
    INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/aubio/
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/aubio/out/${ANDROID_ABI}/usr/local/lib/libaubio.a)

set(AUDIOLIB ffmpeg)
add_library(libavcodec STATIC IMPORTED)
set_target_properties(libavcodec PROPERTIES 
    INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/include/libavcodec
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/lib/libavcodec.a)
add_library(libavformat STATIC IMPORTED)
set_target_properties(libavformat PROPERTIES 
    INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/include/libavformat
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/lib/libavformat.a)
add_library(libswresample STATIC IMPORTED)
set_target_properties(libswresample PROPERTIES 
    INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/include/libswresample
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/lib/libswresample.a)
add_library(libavutil STATIC IMPORTED)
set_target_properties(libavutil PROPERTIES 
    INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/include/libavutil
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/${AUDIOLIB}/out/${ANDROID_ABI}/lib/libavutil.a)

add_library(musalyzer SHARED 
    ${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/BeatDetector.cpp)

target_link_libraries(musalyzer
    ${log-lib}
    libaubio
    libavformat
    libavcodec
    libswresample
    libavutil
)
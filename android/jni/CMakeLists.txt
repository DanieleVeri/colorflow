cmake_minimum_required(VERSION 3.0.0)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/aubio/include/)

find_library(log-lib log)

add_library(libaubio STATIC IMPORTED )
set_target_properties(libaubio PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/aubio/prebuilt/${ANDROID_ABI}/libaubio.a)

add_library(libavcodec STATIC IMPORTED )
set_target_properties(libavcodec PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libavcodec.a)
add_library(libavformat STATIC IMPORTED )
set_target_properties(libavformat PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libavformat.a)
add_library(libavresample STATIC IMPORTED )
set_target_properties(libavresample PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libavresample.a)
add_library(libavutil STATIC IMPORTED )
set_target_properties(libavutil PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libavutil.a)
add_library(libavdevice STATIC IMPORTED )
set_target_properties(libavdevice PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libavdevice.a)
add_library(libavfilter STATIC IMPORTED )
set_target_properties(libavfilter PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libavfilter.a)
add_library(libswscale STATIC IMPORTED )
set_target_properties(libswscale PROPERTIES IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libav/out/${ANDROID_ABI}/lib/libswscale.a)

add_library(musalyzer SHARED 
    ${CMAKE_CURRENT_SOURCE_DIR}/musalyzer/BeatDetector.cpp)

target_link_libraries(musalyzer
    ${log-lib}
    libaubio
    libavformat
    libavcodec
    libavresample
    libswscale
    libavfilter
    libavutil
)
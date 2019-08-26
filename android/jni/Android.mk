LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/lib/libavcodec.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/include/libavcodec
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/lib/libavutil.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/include/libavutil
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavresample
LOCAL_SRC_FILES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/lib/libavresample.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/include/libavresample
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/lib/libavformat.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/libav/out/$(TARGET_ARCH_ABI)/include/libavformat
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := aubio
LOCAL_SRC_FILES := $(LOCAL_PATH)/musalyzer/aubio/prebuilt/$(TARGET_ARCH_ABI)/libaubio.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/musalyzer/aubio/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := musalyzer
LOCAL_LDLIBS := -llog
LOCAL_SRC_FILES :=  musalyzer/BeatDetector.cpp

LOCAL_C_INCLUDES += $(LOCAL_PATH)/musalyzer/
LOCAL_C_INCLUDES += $(LOCAL_PATH)/musalyzer/aubio/include/

LOCAL_STATIC_LIBRARIES :=  aubio libavformat libavcodec libavresample libavutil
include $(BUILD_SHARED_LIBRARY)
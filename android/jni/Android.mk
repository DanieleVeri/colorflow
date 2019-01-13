LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include $(CLEAR_VARS)
LOCAL_MODULE := aubio
LOCAL_SRC_FILES := $(LOCAL_PATH)/beatdetector/aubio/prebuilt/$(TARGET_ARCH_ABI)/libaubio.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/beatdetector/aubio/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := beatdetector
LOCAL_LDLIBS := -llog
LOCAL_SRC_FILES := beatdetector/main.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)/beatdetector/
LOCAL_C_INCLUDES += $(LOCAL_PATH)/beatdetector/aubio/include/
LOCAL_STATIC_LIBRARIES := aubio
include $(BUILD_SHARED_LIBRARY)

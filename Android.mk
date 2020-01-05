LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
aidl/com/xxun/watch/storytall/IMyAidlBinderStory.aidl \

SRC_ROOT := java/com/xxun/watch/storytall

LOCAL_PACKAGE_NAME := XunStoryTell

#LOCAL_OVERRIDES_PACKAGES := Settings

LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES :=framework

#LOCAL_PRIVILEGED_MODULE := true

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-annotations \
    android-support-v4 \
    android-support-v7-appcompat \
    android-support-v7-recyclerview \

LOCAL_SDK_VERSION := current

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))

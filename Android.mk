LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src)

LOCAL_RESOURCE_DIR += \
    $(LOCAL_PATH)/res

#LOCAL_STATIC_JAVA_LIBRARIES += \
    mega.sdk.system

LOCAL_PACKAGE_NAME := MegaBluetoothHid
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_CERTIFICATE := platform
LOCAL_DEX_PREOPT := false

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := native-lib

LOCAL_SRC_FILES :=  native-lib.cpp
LOCAL_LDLIBS    += -llog -ldl -lz -landroid -v
LOCAL_CFLAGS	+= -DOS_ANDROID -DFILE_OFFSET_BITS=64 -DLARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64 -D_LARGEFILE64_SOURCE -Wno-psabi -O1 -Wgs
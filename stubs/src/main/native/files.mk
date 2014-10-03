WAYLAND_JNI_SERVER_SRC := \
	src/server/WlServerJNI.c\
	src/util/WlUtilJNI.c

WAYLAND_JNI_CLIENT_SRC := \
	src/client/WlClientJNI.c\
	src/util/WlUtilJNI.c
	
WAYLAND_JNI_EGL_SRC := \
	src/client/WlEglJNI.c

WAYLAND_JNI_CFLAGS = -Wall
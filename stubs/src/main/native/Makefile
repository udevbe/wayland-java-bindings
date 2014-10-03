include files.mk

BUILD_DIR := ../resources
$(shell mkdir -p $(BUILD_DIR))
OBJECT_DIR := build/objects

ifeq ($(JAVA_HOME),)
JAVA_HOME = $(realpath $(dir $(realpath $(shell which javac)))/../ )
endif

JNI_CFLAGS := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

WAYLAND_CFLAGS := $(CFLAGS) $(shell pkg-config --cflags wayland-server wayland-egl wayland-client)

CFLAGS := -g -fPIC $(WAYLAND_JNI_CFLAGS) $(WAYLAND_CFLAGS) $(JNI_CFLAGS)

SERVER_LDFLAGS := $(shell pkg-config --libs wayland-server)
CLIENT_LDFLAGS := $(shell pkg-config --libs wayland-client)
EGL_LDFLAGS := $(shell pkg-config --libs wayland-egl)

SERVER_OBJECTS := $(foreach file, $(WAYLAND_JNI_SERVER_SRC), \
		$(patsubst src/%.c, $(OBJECT_DIR)/%.o, $(file)))
CLIENT_OBJECTS := $(foreach file, $(WAYLAND_JNI_CLIENT_SRC), \
		$(patsubst src/%.c, $(OBJECT_DIR)/%.o, $(file)))
EGL_OBJECTS := $(foreach file, $(WAYLAND_JNI_EGL_SRC), \
		$(patsubst src/%.c, $(OBJECT_DIR)/%.o, $(file)))

all: $(BUILD_DIR)/libwayland-java-server.so $(BUILD_DIR)/libwayland-java-client.so $(BUILD_DIR)/libwayland-java-egl.so

$(OBJECT_DIR)%.o: src/%.c
	mkdir -p $(dir $@)
	gcc $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/libwayland-java-server.so: $(SERVER_OBJECTS)
	gcc -shared -Wl,-soname,libwayland-java-server.so -o $@ $^ $(SERVER_LDFLAGS)

$(BUILD_DIR)/libwayland-java-client.so: $(CLIENT_OBJECTS)
	gcc -shared -Wl,-soname,libwayland-java-client.so -o $@ $^ $(CLIENT_LDFLAGS)
	
$(BUILD_DIR)/libwayland-java-egl.so: $(EGL_OBJECTS)
	gcc -shared -Wl,-soname,libwayland-java-egl.so -o $@ $^ $(EGL_LDFLAGS)

.PHONY: clean
clean:
	rm -rf $(OBJECT_DIR)
	rm -f $(BUILD_DIR)/libwayland-java-server.so
	rm -f $(BUILD_DIR)/libwayland-java-client.so
	rm -f $(BUILD_DIR)/libwayland-java-egl.so


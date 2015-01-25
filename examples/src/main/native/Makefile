#Copyright 2015 Erik De Rijcke
#
#Licensed under the Apache License,Version2.0(the"License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing,software
#distributed under the License is distributed on an"AS IS"BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.

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
CFLAGS += $(foreach file, $(WAYLAND_JNI_C_INCLUDES), -I$(file))

SERVER_LDFLAGS := $(shell pkg-config --libs wayland-server)
SERVER_LDFLAGS += -L$(BUILD_DIR) -lwayland-java-util
CLIENT_LDFLAGS := $(shell pkg-config --libs wayland-client)
CLIENT_LDFLAGS += -L$(BUILD_DIR) -lwayland-java-util

UTIL_OBJECTS := $(foreach file, $(WAYLAND_JNI_UTIL_SRC), \
		$(patsubst src/%.c, $(OBJECT_DIR)/%.o, $(file)))

all: $(BUILD_DIR)/libwayland-java-util.so

$(OBJECT_DIR)%.o: src/%.c
	mkdir -p $(dir $@)
	gcc $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/libwayland-java-util.so: $(UTIL_OBJECTS)
	gcc -shared -Wl,-soname,libwayland-java-util.so -o $@ $^ $(UTIL_LDFLAGS)

.PHONY: clean
clean:
	rm -rf $(OBJECT_DIR)
	rm -f $(BUILD_DIR)/libwayland-java-server.so
	rm -f $(BUILD_DIR)/libwayland-java-client.so


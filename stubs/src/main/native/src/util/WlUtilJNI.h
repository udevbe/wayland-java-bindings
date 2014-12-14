#ifndef WLUTILJNI_H_
#define WLUTILJNI_H_

#include "org_freedesktop_wayland_util_WlUtilJNI.h"

extern JavaVM* jvm;

#define GET_ATTACHED_JENV(jenv){\
    (*jvm)->GetEnv(jvm, (void **)&jenv,JNI_VERSION_1_6);\
    JavaVMAttachArgs vm_args;\
    vm_args.version = JNI_VERSION_1_6;\
    vm_args.name = NULL;\
    vm_args.group = NULL;\
    (*jvm)->AttachCurrentThread(jvm, (void **) &jenv, &vm_args);\
}

int wl_dispatcher_func(const void *,
                       void *,
                       uint32_t,
                       const struct wl_message *,
                       union wl_argument *);

#endif /* WLUTILJNI_H_ */
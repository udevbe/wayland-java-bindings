//Copyright 2015 Erik De Rijcke
//
//Licensed under the Apache License,Version2.0(the"License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing,software
//distributed under the License is distributed on an"AS IS"BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package examples;

import java.io.IOException;

public class SimpleShm {

    public static void main(final String[] args) throws IOException {

//        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(final Thread thread, final Throwable throwable) {
//                System.err.println(throwable.getCause().getMessage());
//                throwable.printStackTrace();
//            }
//        });

        final Display display = new Display();
        final Window window = new Window(display,
                                         250,
                                         250);
        window.redraw(0);

        try {
            int state;
            do {
                state = display.getDisplayProxy()
                               .dispatch();
            }while(state!=-1);
        }finally {
            window.destroy();
            display.destroy();
        }
    }
}

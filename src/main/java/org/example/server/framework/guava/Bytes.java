package org.example.server.framework.guava;

public class Bytes {
    private Bytes() {
    }

    // From Guava
    public static int indexOf(byte[] array, byte[] target, int offset) {
        if (target.length == 0) {
            return 0;
        } else {
            outer:
            for (int i = offset; i < array.length - target.length + 1; ++i) {
                for (int j = 0; j < target.length; ++j) {
                    if (array[i + j] != target[j]) {
                        continue outer;
                    }
                }

                return i;
            }

            return -1;
        }
    }
}

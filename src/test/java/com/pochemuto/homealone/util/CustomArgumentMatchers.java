package com.pochemuto.homealone.util;

import java.util.Collection;

import org.mockito.ArgumentMatcher;

public class CustomArgumentMatchers {
    private CustomArgumentMatchers() {
    }

    public static <T extends Collection<?>> ArgumentMatcher<T> notEmpty() {
        return new ArgumentMatcher<>() {
            @Override
            public boolean matches(T argument) {
                return argument != null && !argument.isEmpty();
            }

            @Override
            public String toString() {
                return "<not empty>";
            }
        };
    }
}

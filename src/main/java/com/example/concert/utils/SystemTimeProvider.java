package com.example.concert.utils;

import java.time.LocalDateTime;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
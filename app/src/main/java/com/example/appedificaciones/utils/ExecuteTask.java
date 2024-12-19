package com.example.appedificaciones.utils;

public class ExecuteTask {
    public void asyncTask(Runnable runnable) {
        new Thread(() -> {
            runnable.run();
        }).start();
    }
}

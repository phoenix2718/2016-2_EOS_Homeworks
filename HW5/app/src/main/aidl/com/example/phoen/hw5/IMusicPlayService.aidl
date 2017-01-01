// IMusicPlayService.aidl
package com.example.phoen.hw5;

// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void pause();
    void resume();
    void back();
    boolean isPlaying();
    boolean isStarted();
}

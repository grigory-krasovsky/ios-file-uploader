package com.example.iosfileuploader.core.utils;

public interface SystemParameterManager {
    <T> T getParam(String paramName, Class<T> type);
}

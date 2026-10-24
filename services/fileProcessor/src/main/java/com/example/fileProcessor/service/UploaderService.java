package com.example.fileProcessor.service;

public interface UploaderService {

    void upload(String message, String groupId);

    void sendMessage(String message, String  groupId);

}

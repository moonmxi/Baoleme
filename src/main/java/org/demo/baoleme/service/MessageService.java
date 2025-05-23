package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Message;

import java.util.List;

public interface MessageService {

    void saveMessage(Message message);

    List<Message> getChatHistory(Long senderId, String senderRole,
                                 Long receiverId, String receiverRole,
                                 int page, int pageSize);
}
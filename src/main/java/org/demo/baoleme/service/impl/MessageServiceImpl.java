package org.demo.baoleme.service.impl;

import org.demo.baoleme.mapper.MessageMapper;
import org.demo.baoleme.pojo.Message;
import org.demo.baoleme.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void saveMessage(Message message) {
        messageMapper.insert(message);
    }

    @Override
    public List<Message> getChatHistory(Long senderId, String senderRole,
                                        Long receiverId, String receiverRole,
                                        int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return messageMapper.selectChatHistory(senderId, senderRole, receiverId, receiverRole, offset, pageSize);
    }
}
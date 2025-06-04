package org.demo.baoleme.chat;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.common.UserHolder;
import org.demo.baoleme.pojo.Message;
import org.demo.baoleme.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 获取聊天记录
     */
    @PostMapping("/history")
    public CommonResponse getChatHistory(@Valid @RequestBody ChatHistoryRequest request) {
        Long senderId = UserHolder.getId();
        String senderRole = UserHolder.getRole();

        List<Message> history = messageService.getChatHistory(
                senderId,
                senderRole,
                request.getReceiver_id(),
                request.getReceiver_role(),
                request.getPage(),
                request.getPage_size()
        );

        List<ChatMessageResponse> responses = history.stream().map(msg -> {
            ChatMessageResponse resp = new ChatMessageResponse();
            BeanUtils.copyProperties(msg, resp);
            return resp;
        }).collect(Collectors.toList());

        return ResponseBuilder.ok(Map.of("messages", responses));
    }
}
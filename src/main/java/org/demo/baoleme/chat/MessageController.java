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
     *
     * @param request 包含接收方信息和分页参数的请求体
     * @return 包含历史消息列表的响应
     */
    @PostMapping("/history")
    public CommonResponse getChatHistory(@Valid @RequestBody ChatHistoryRequest request) {
        // Step 1: 获取当前用户身份信息
        Long senderId = UserHolder.getId();
        String senderRole = UserHolder.getRole();

        // Step 2: 调用服务层获取历史消息
        List<Message> history = messageService.getChatHistory(
                senderId,
                senderRole,
                request.getReceiver_id(),
                request.getReceiver_role(),
                request.getPage(),
                request.getPage_size()
        );

        // Step 3: 转换实体对象为响应DTO
        List<ChatMessageResponse> responses = history.stream().map(msg -> {
            ChatMessageResponse resp = new ChatMessageResponse();

            // Step 3.1: 复制属性到响应对象
            BeanUtils.copyProperties(msg, resp);

            return resp;
        }).toList();

        // Step 4: 构建成功响应
        return ResponseBuilder.ok(Map.of("messages", responses));
    }
}
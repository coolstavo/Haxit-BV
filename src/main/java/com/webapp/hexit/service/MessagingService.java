package com.webapp.hexit.service;

import com.webapp.hexit.model.Message;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.MessageRepository;
import com.webapp.hexit.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessagingService
{

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MessagingService(UserRepository userRepository, MessageRepository messageRepository)
    {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    private User requireUser(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public long getTotalUnread(String username)
    {
        User me = requireUser(username);
        return messageRepository.countByReceiverAndReadAtIsNull(me);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getUnreadByPartner(String username)
    {
        User me = requireUser(username);
        List<String> partners = messageRepository.findChatPartnersUsernames(me);

        Map<String, Long> result = new HashMap<>();
        for (String p : partners)
        {
            User partner = requireUser(p);
            long count = messageRepository.countBySenderAndReceiverAndReadAtIsNull(partner, me);
            result.put(p, count);
        }
        return result;
    }

    @Transactional
    public int markRead(String meUsername, String otherUsername)
    {
        User me = requireUser(meUsername);
        User other = requireUser(otherUsername);
        return messageRepository.markReadFromOther(me, other, LocalDateTime.now());
    }

    @Transactional
    public void send(String fromUsername, String toUsername, String content)
    {
        User from = requireUser(fromUsername);
        User to = requireUser(toUsername);

        Message m = new Message(from, to, content, LocalDateTime.now());
        messageRepository.save(m);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(String aUsername, String bUsername)
    {
        User a = requireUser(aUsername);
        User b = requireUser(bUsername);
        return messageRepository.findConversation(a, b);
    }

    @Transactional(readOnly = true)
    public List<String> getPartners(String username)
    {
        User me = requireUser(username);
        return messageRepository.findChatPartnersUsernames(me);
    }
}

package com.webapp.hexit.service;

import com.webapp.hexit.model.Message;
import com.webapp.hexit.model.User;
import com.webapp.hexit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryMessagingService
{
    private final UserRepository userRepository;

    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> inboxIndex = new ConcurrentHashMap<>();

    public InMemoryMessagingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String key(String a, String b) {
        return (a.compareToIgnoreCase(b) <= 0) ? (a + "|" + b) : (b + "|" + a);
    }

    public void send(String fromUsername, String toUsername, String content)
    {
        User from = userRepository.findByUsername(fromUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + fromUsername));
        User to = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + toUsername));

        Message m = new Message(from, to, content, LocalDateTime.now());

        String k = key(fromUsername, toUsername);
        conversations.computeIfAbsent(k, _k -> Collections.synchronizedList(new ArrayList<>()))
                .add(m);

        inboxIndex.computeIfAbsent(fromUsername, _u -> ConcurrentHashMap.newKeySet()).add(toUsername);
        inboxIndex.computeIfAbsent(toUsername, _u -> ConcurrentHashMap.newKeySet()).add(fromUsername);
    }

    public List<Message> getConversation(String a, String b)
    {
        return conversations.getOrDefault(key(a, b), List.of());
    }

    public List<String> getPartners(String username)
    {
        Set<String> set = inboxIndex.getOrDefault(username, Set.of());
        List<String> list = new ArrayList<>(set);
        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }
}

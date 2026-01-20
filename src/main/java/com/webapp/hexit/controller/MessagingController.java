package com.webapp.hexit.controller;

import com.webapp.hexit.model.Message;
import com.webapp.hexit.service.MessagingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessagingController
{
    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    private String currentUser(HttpSession session) {
        Object u = session.getAttribute("currentUsername");
        return u == null ? null : u.toString();
    }

    @GetMapping("/messages")
    public String inbox(HttpSession session, Model model) {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";

        model.addAttribute("me", me);
        model.addAttribute("partners", messagingService.getPartners(me));
        model.addAttribute("selectedPartner", null);
        model.addAttribute("messages", List.of());
        model.addAttribute("unreadByPartner", messagingService.getUnreadByPartner(me));
        return "messages";
    }

    @PostMapping("/messages/start")
    public String start(@RequestParam String other, HttpSession session) {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";

        String cleaned = other == null ? "" : other.trim();
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(me)) return "redirect:/messages";

        return "redirect:/messages/" + cleaned;
    }

    @GetMapping("/messages/{other}")
    public String conversation(@PathVariable String other, HttpSession session, Model model) {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";
        if (other == null || other.isBlank() || other.equalsIgnoreCase(me)) return "redirect:/messages";

        messagingService.markRead(me, other);

        List<Message> convo = messagingService.getConversation(me, other);

        model.addAttribute("me", me);
        model.addAttribute("partners", messagingService.getPartners(me));
        model.addAttribute("selectedPartner", other);
        model.addAttribute("messages", convo);
        model.addAttribute("unreadByPartner", messagingService.getUnreadByPartner(me));
        return "messages";
    }

    @PostMapping("/messages/{other}")
    public String send(@PathVariable String other,
                       @RequestParam String content,
                       HttpSession session) {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";
        if (other == null || other.isBlank() || other.equalsIgnoreCase(me)) return "redirect:/messages";

        String text = content == null ? "" : content.trim();
        if (!text.isEmpty()) {
            messagingService.send(me, other, text);
        }
        return "redirect:/messages/" + other;
    }

    @GetMapping("/messages/{other}/partial")
    public String conversationPartial(@PathVariable String other, HttpSession session, Model model) {
        String me = currentUser(session);
        if (me == null || other == null || other.isBlank() || other.equalsIgnoreCase(me)) {
            model.addAttribute("me", "");
            model.addAttribute("selectedPartner", null);
            model.addAttribute("messages", List.of());
            return "fragments/_messageThread :: thread";
        }

        model.addAttribute("me", me);
        model.addAttribute("selectedPartner", other);
        model.addAttribute("messages", messagingService.getConversation(me, other));
        return "fragments/_messageThread :: thread";
    }

    @GetMapping("/messages/unread-count")
    @ResponseBody
    public long unreadCount(HttpSession session) {
        String me = currentUser(session);
        if (me == null) return 0;
        return messagingService.getTotalUnread(me);
    }

    @GetMapping("/messages/debug")
    @ResponseBody
    public String debugSession(HttpSession session) {
        return "currentUsername=" + session.getAttribute("currentUsername")
                + " | currentUserRole=" + session.getAttribute("currentUserRole")
                + " | username=" + session.getAttribute("username")
                + " | userRole=" + session.getAttribute("userRole");
    }

    @GetMapping("/debug/messages/send")
    @ResponseBody
    public String debugSend(@RequestParam String from,
                            @RequestParam String to,
                            @RequestParam(defaultValue = "Testbericht") String content) {
        messagingService.send(from, to, content);
        return "OK sent from=" + from + " to=" + to + " content=" + content;
    }

    @GetMapping("/debug/messages/unread-total")
    @ResponseBody
    public String debugUnreadTotal(@RequestParam String user) {
        long c = messagingService.getTotalUnread(user);
        return "unreadTotal(" + user + ")=" + c;
    }

    @GetMapping("/debug/messages/unread-with")
    @ResponseBody
    public String debugUnreadWith(@RequestParam String me,
                                  @RequestParam String other) {
        // unread messages that other -> me
        var map = messagingService.getUnreadByPartner(me);
        Long c = map.get(other);
        return "unreadFrom(" + other + "->" + me + ")=" + (c == null ? 0 : c);
    }

    @GetMapping("/debug/messages/markread")
    @ResponseBody
    public String debugMarkRead(@RequestParam String me,
                                @RequestParam String other) {
        int updated = messagingService.markRead(me, other);
        long after = messagingService.getTotalUnread(me);
        return "markRead updated=" + updated + " | unreadTotal(" + me + ")=" + after;
    }
}

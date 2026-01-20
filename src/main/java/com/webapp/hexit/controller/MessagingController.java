package com.webapp.hexit.controller;

import com.webapp.hexit.model.Message;
import com.webapp.hexit.service.InMemoryMessagingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessagingController
{
    private final InMemoryMessagingService messagingService;

    public MessagingController(InMemoryMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    private String currentUser(HttpSession session)
    {
        Object u = session.getAttribute("currentUsername");
        return u == null ? null : u.toString();
    }

    @GetMapping("/messages")
    public String inbox(HttpSession session, Model model)
    {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";

        model.addAttribute("partners", messagingService.getPartners(me));
        model.addAttribute("selectedPartner", null);
        model.addAttribute("messages", List.of());
        return "messages";
    }

    @PostMapping("/messages/start")
    public String start(@RequestParam String other, HttpSession session) {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";

        String cleaned = other == null ? "" : other.trim();
        if (cleaned.isEmpty() || cleaned.equalsIgnoreCase(me)) return "redirect:/messages";

        // We don't create anything yet; just open the conversation route.
        return "redirect:/messages/" + cleaned;
    }

    @GetMapping("/messages/{other}")
    public String conversation(@PathVariable String other, HttpSession session, Model model)
    {
        String me = currentUser(session);
        if (me == null) return "redirect:/login-page";
        if (other == null || other.isBlank() || other.equalsIgnoreCase(me)) return "redirect:/messages";

        List<Message> convo = messagingService.getConversation(me, other);

        model.addAttribute("partners", messagingService.getPartners(me));
        model.addAttribute("selectedPartner", other);
        model.addAttribute("messages", convo);
        return "messages";
    }

    @PostMapping("/messages/{other}")
    public String send(@PathVariable String other,
                       @RequestParam String content,
                       HttpSession session)
    {
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
        if (me == null) return "redirect:/login-page";
        if (other == null || other.isBlank() || other.equalsIgnoreCase(me)) return "redirect:/messages";

        model.addAttribute("me", me);
        model.addAttribute("selectedPartner", other);
        model.addAttribute("messages", messagingService.getConversation(me, other));
        return "fragments/_messageThread :: thread";
    }

    @GetMapping("/messages/debug")
    @ResponseBody
    public String debugSession(HttpSession session) {
        return "currentUsername=" + session.getAttribute("currentUsername")
                + " | currentUserRole=" + session.getAttribute("currentUserRole")
                + " | username=" + session.getAttribute("username")
                + " | userRole=" + session.getAttribute("userRole");
    }


}

package com.webapp.hexit.controller;

import com.webapp.hexit.service.MessagingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionUserAdvice
{
    private final MessagingService messagingService;

    public SessionUserAdvice(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @ModelAttribute("username")
    public String username(HttpSession session)
    {
        Object u = session.getAttribute("currentUsername");
        return u == null ? "Gast" : u.toString();
    }

    @ModelAttribute("userRole")
    public String userRole(HttpSession session)
    {
        Object r = session.getAttribute("currentUserRole");
        return r == null ? "GAST" : r.toString();
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(HttpSession session) {
        Object u = session.getAttribute("currentUsername");
        if (u == null) return 0;
        return messagingService.getTotalUnread(u.toString());
    }
}

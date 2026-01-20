package com.webapp.hexit.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionUserAdvice
{
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
}

package com.paymetv.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Catches GET requests for frontend SPA routes (e.g. /upload) that are not
 * handled by the static resource handler or any API controller, and forwards
 * them to index.html so React can take over client-side routing.
 *
 * Pattern rules:
 *  - Must NOT start with /api  (those are backend endpoints)
 *  - Must NOT start with /assets, /vite.svg etc. (static files served directly)
 */
@Controller
public class SpaForwardController {

    /**
     * Forward the /upload path to index.html for React SPA routing.
     */
    @GetMapping("/upload")
    public String uploadPage() {
        return "forward:/index.html";
    }
}

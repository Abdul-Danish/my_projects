//package com.bank.controller;
//
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//public class HomeController {
//
//    @GetMapping("/")
//    public String devHome(Model model, @AuthenticationPrincipal OidcUser principle) {
//        if (principle != null) {
//            model.addAttribute("profile", principle.getClaims());
//        }
//        
//        return "index";
//    }
//    
////    @GetMapping("/test")
////    public String testHome(Model model, @AuthenticationPrincipal OidcUser principle) {
////        if (principle != null) {
////            model.addAttribute("profile", principle.getClaims());
////        }
////        
////        return "index";
////    }
//    
//    
//}

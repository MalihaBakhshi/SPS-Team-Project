package com.sps.hobbymatcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.sps.hobbymatcher.domain.User;
import com.sps.hobbymatcher.domain.Post;
import com.sps.hobbymatcher.domain.Hobby;
import com.sps.hobbymatcher.service.UserService;
import com.sps.hobbymatcher.service.HobbyService;
import com.sps.hobbymatcher.service.PostService;
import com.sps.hobbymatcher.repository.HobbyRepository;
import com.sps.hobbymatcher.repository.PostRepository;

@Controller
public class HobbiesController {

    @Autowired
    private UserService userService;

    @Autowired
    private HobbyService hobbyService;
    
    @Autowired
    private PostService postService;

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private PostRepository postRepository;
    
    @GetMapping("/hobbies")
    public String hobbies(ModelMap model) {   
        
        Set<Hobby> hobbies = hobbyRepository.findAll();
        model.put("hobbies", hobbies);

        return "hobbies";
    }

    @PostMapping("/hobbies/{hobbyId}/register")
    public String register(@PathVariable Long hobbyId, @AuthenticationPrincipal User user) {   
        
        Optional<Hobby> hobby = hobbyRepository.findById(hobbyId);
        userService.addHobby(user, hobby);
        
        return "redirect: /hobbies/"+hobbyId;
    }

    @PostMapping("/hobbies/{hobbyId}/unregister")
    public String unregister(@PathVariable Long hobbyId, @AuthenticationPrincipal User user) {   
        
        Optional<Hobby> hobbyOpt = hobbyRepository.findById(hobbyId);
        if(hobbyOpt.isPresent()) {
            Hobby hobby=hobbyOpt.get();
            userService.removeHobby(user, hobby);
        }
        return "redirect: /hobbies/"+hobbyId;
    }

    @GetMapping("/hobbies/{hobbyId}/users")
    public String users(@PathVariable Long hobbyId, ModelMap model) {   
        
        Optional<Hobby> hobbyOpt = hobbyRepository.findById(hobbyId);
        if(hobbyOpt.isPresent()) {
            Hobby hobby = hobbyOpt.get();
            Set<Long> users = hobby.getUsers();
            model.put("users", users);
        }
        return "hobby";
    }

    @GetMapping("/hobbies/{hobbyId}")
    public String posts(@PathVariable Long hobbyId, ModelMap model) {   
        
        Optional<Hobby> hobbyOpt = hobbyRepository.findById(hobbyId);
        if(hobbyOpt.isPresent()) {
            Hobby hobby = hobbyOpt.get();
            Set<Post> posts = hobby.getPosts();
            model.put("posts", posts);
        }
        return "hobby";
    }

    @GetMapping("/createhobby/{hobbyId}")
    public String createHobby(@PathVariable Long hobbyId, ModelMap model, HttpServletResponse response) throws IOException {

        Optional<Hobby> hobbyOpt = hobbyRepository.findById(hobbyId);
        if(hobbyOpt.isPresent()) {
            Hobby hobby = hobbyOpt.get();
            model.put("hobby", hobby);
        }
        
        return "hobby";
    }

    @PostMapping("/createhobby/{hobbyId}")
    public String saveHobby(@PathVariable Long hobbyId, Hobby hobby) {   
        hobby = hobbyService.save(hobby);
        return "redirect: /createhobby/"+hobby.getId();
    }

    @PostMapping("/createhobby")
    public String createHobby(@AuthenticationPrincipal User user) {   
        Hobby hobby=new Hobby();
        hobby.getUsers().add(user.getId());
        hobby = hobbyRepository.save(hobby);
        user.getMyHobbies().add(hobby.getId());
        return "redirect: /createhobby/"+hobby.getId();
    }
}
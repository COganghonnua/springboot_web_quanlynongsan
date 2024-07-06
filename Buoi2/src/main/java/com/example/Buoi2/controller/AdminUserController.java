package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Role;
import com.example.Buoi2.entity.User;
import com.example.Buoi2.services.RoleService;
import com.example.Buoi2.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    @Autowired
    private UserServices userServices;

    @Autowired
    private RoleService roleServices;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userServices.findAll();
        model.addAttribute("users", users);
        return "admin/user-list";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        List<Role> roles = roleServices.findAll();
        model.addAttribute("roles", roles);
        return "admin/user-form";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam List<Long> roleIds, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/user-form";
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        List<Role> roles = roleServices.findByIds(roleIds);
        user.setRoles(roles);
        userServices.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userServices.findById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        List<Role> roles = roleServices.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "admin/user-form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam List<Long> roleIds, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/user-form";
        }
        User existingUser = userServices.findById(id);
        if (existingUser == null) {
            return "redirect:/admin/users";
        }
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setAddress(user.getAddress());
        existingUser.setBirthdate(user.getBirthdate());
        List<Role> roles = roleServices.findByIds(roleIds);
        existingUser.setRoles(roles);
        userServices.save(existingUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userServices.deleteById(id);
        return "redirect:/admin/users";
    }
}

package com.trick.admin.controller;

import com.trick.admin.model.dto.AdminDTO;
import com.trick.admin.model.pojo.Admin;
import com.trick.admin.service.LoginService;
import com.trick.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody AdminDTO adminDTO) {

        return Result.success(loginService.loginAdmin(adminDTO));
    }
}
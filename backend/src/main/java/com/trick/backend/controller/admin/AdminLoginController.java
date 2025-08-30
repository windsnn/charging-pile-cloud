package com.trick.backend.controller.admin;

import com.trick.backend.common.result.Result;
import com.trick.backend.model.dto.LoginDTO;
import com.trick.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
public class AdminLoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {

        return Result.success(loginService.loginAdmin(dto));
    }
}

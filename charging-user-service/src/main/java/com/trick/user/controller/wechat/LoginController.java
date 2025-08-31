package com.trick.user.controller.wechat;

import com.trick.common.result.Result;
import com.trick.user.model.dto.LoginDTO;
import com.trick.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wx/auth")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) throws Exception {
        String token = loginService.loginUser(dto.getCode());
        return Result.success(token);
    }

}

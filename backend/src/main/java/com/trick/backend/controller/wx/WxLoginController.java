package com.trick.backend.controller.wx;

import com.trick.backend.common.result.Result;
import com.trick.backend.model.dto.LoginDTO;
import com.trick.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wx/auth")
public class WxLoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) throws Exception {
        String token = loginService.loginUser(dto.getCode());
        return Result.success(token);
    }

}

package com.trick.backend.controller.admin;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.common.result.Result;
import com.trick.backend.model.dto.ChargingPileQueryDTO;
import com.trick.backend.model.dto.UserQueryDTO;
import com.trick.backend.model.pojo.ChargingPile;
import com.trick.backend.model.pojo.User;
import com.trick.backend.model.vo.UserVO;
import com.trick.backend.service.UserService;
import com.trick.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    Result<PageResult<User>> getUsersByPage(UserQueryDTO queryDTO) {
        return Result.success(userService.getUsersByPage(queryDTO));
    }

    @GetMapping("/{id}")
    Result<UserVO> getUserById(@PathVariable Integer id) {
        return Result.success(userService.getUserById(id));
    }

}

package com.trick.user.controller.admin;


import com.trick.common.result.PageResult;
import com.trick.common.result.Result;
import com.trick.user.model.dto.UserQueryDTO;
import com.trick.user.model.pojo.User;
import com.trick.user.model.vo.UserVO;
import com.trick.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
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

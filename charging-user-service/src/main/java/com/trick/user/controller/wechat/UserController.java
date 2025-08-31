package com.trick.user.controller.wechat;

import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.user.model.dto.UserAddAndUpdateDTO;
import com.trick.user.model.vo.WxUserProfileVO;
import com.trick.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx/user/profile")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    // 获取user信息
    public Result<WxUserProfileVO> getUserProfile() {
        //获取token id
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        return Result.success(userService.getUserProfileById(userId));
    }

    //更新user信息
    @PutMapping
    public Result<?> updateUserProfile(@RequestBody UserAddAndUpdateDTO userAddAndUpdateDTO) {
        //获取token id
        Integer userId = (Integer) ThreadLocalUtil.getContext().get("id");

        userAddAndUpdateDTO.setId(userId);
        userService.updateUser(userAddAndUpdateDTO);
        return Result.success();
    }
}

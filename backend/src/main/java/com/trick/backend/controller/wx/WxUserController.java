package com.trick.backend.controller.wx;

import com.trick.backend.common.result.PageResult;
import com.trick.backend.common.result.Result;
import com.trick.backend.common.utils.ThreadLocalUtil;
import com.trick.backend.model.dto.UserAddAndUpdateDTO;
import com.trick.backend.model.vo.ChargingOrderVO;
import com.trick.backend.model.vo.WxUserProfileVO;
import com.trick.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wx/user/profile")
public class WxUserController {
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

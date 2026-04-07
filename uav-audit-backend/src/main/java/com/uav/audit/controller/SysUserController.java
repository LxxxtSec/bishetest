package com.uav.audit.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.uav.audit.aspect.AuditRecord;
import com.uav.audit.dto.Result;
import com.uav.audit.entity.SysUser;
import com.uav.audit.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @AuditRecord(operationType = "USER_QUERY", description = "查询用户列表", operationObject = "用户管理")
    public Result<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        IPage<SysUser> page = sysUserService.getUserPage(pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    @PostMapping
    @AuditRecord(operationType = "USER_CREATE", description = "创建用户", operationObject = "用户管理")
    public Result<Void> createUser(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode("123456"));
        user.setStatus(1); // 默认正常状态
        sysUserService.save(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @AuditRecord(operationType = "USER_UPDATE", description = "更新用户", operationObject = "用户管理")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AuditRecord(operationType = "USER_DELETE", description = "删除用户", operationObject = "用户管理")
    public Result<Void> deleteUser(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }
}

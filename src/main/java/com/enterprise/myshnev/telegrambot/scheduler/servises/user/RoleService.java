package com.enterprise.myshnev.telegrambot.scheduler.servises.user;

import com.enterprise.myshnev.telegrambot.scheduler.model.Role;

public interface RoleService {
    Role findRoleByName(String role);
}

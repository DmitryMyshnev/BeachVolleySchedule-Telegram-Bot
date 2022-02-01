package com.enterprise.myshnev.telegrambot.scheduler.repository;

import com.enterprise.myshnev.telegrambot.scheduler.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String role);
}

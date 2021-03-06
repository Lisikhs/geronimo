package com.geronimo.config;

import com.geronimo.model.Role;
import com.geronimo.model.User;
import com.geronimo.service.IRoleService;
import com.geronimo.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class Bootstrap {

    private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    private static final String USER_ROLE_NAME = "ROLE_USER";

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        createRolesAndPermissions();
        createRootUser();
        createJohnDoeUser();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createRolesAndPermissions() {
        Role adminRole = roleService.findRoleByName(ADMIN_ROLE_NAME);
        if (adminRole == null) {
            adminRole = new Role(ADMIN_ROLE_NAME);
            roleService.saveRole(adminRole);
        }

        Role userRole = roleService.findRoleByName(USER_ROLE_NAME);
        if (userRole == null) {
            userRole = new Role(USER_ROLE_NAME);
            roleService.saveRole(userRole);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createRootUser() {
        User user = userService.getByUsername("root");
        if (user == null) {
            log.info("Creating root user since it's not in the database yet");
            user = new User("root", passwordEncoder.encode("root"));
            user.getRoles().add(roleService.findRoleByName(ADMIN_ROLE_NAME));

            userService.saveOrUpdateUser(user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createJohnDoeUser() {
        User user = userService.getByUsername("john");
        if (user == null) {
            user = new User("john", passwordEncoder.encode("doe"));
            user.getRoles().add(roleService.findRoleByName(USER_ROLE_NAME));

            userService.saveOrUpdateUser(user);
        }
    }
}

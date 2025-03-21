package com.alisimsek.taskmanagement.initializer;

import com.alisimsek.taskmanagement.role.entity.UserPermission;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import com.alisimsek.taskmanagement.role.repository.UserRoleRespository;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.entity.UserType;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRoleRespository userRoleRespository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        UserRole adminUserRole= userRoleRespository.save(getAdminUserRole());
        User adminUser = getAdminUser();
        adminUser.setRoles(Set.of(adminUserRole));
        userRepository.save(adminUser);

        UserRole projectManagerUserRole= userRoleRespository.save(getProjectManagerUserRole());
        User projectManagerUser = getProjectManagerUser();
        projectManagerUser.setRoles(Set.of(projectManagerUserRole));
        userRepository.save(projectManagerUser);

        UserRole teamLeaderUserRole= userRoleRespository.save(getTeamLeaderUserRole());
        User teamLeaderUser = getTeamLeaderUser();
        teamLeaderUser.setRoles(Set.of(teamLeaderUserRole));
        userRepository.save(teamLeaderUser);

        UserRole teamMemberUserRole= userRoleRespository.save(getTeamMemberUserRole());
        User teamMemberUser = getTeamMemberUser();
        teamMemberUser.setRoles(Set.of(teamMemberUserRole));
        userRepository.save(teamMemberUser);
    }

    private UserRole getAdminUserRole() {
        UserRole userRole = new UserRole();
        userRole.setName("super-admin-role");
        userRole.setDescription("super-admin-role");
        userRole.setUserPermissions(Set.of(
                UserPermission.MANAGE_PROJECTS,
                UserPermission.MANAGE_TASKS,
                UserPermission.ASSIGN_TEAM_MEMBER,
                UserPermission.UPDATE_TASK_STATE,
                UserPermission.SET_TASK_PRIORITY,
                UserPermission.ADD_ATTACHMENT
        ));
        return userRole;
    }
    private User getAdminUser() {
        User user = new User();
        user.setUsername("admin@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("AdminFirstname");
        user.setLastName("AdminLastname");
        user.setUserType(UserType.ADMIN);
        return user;
    }

    private UserRole getProjectManagerUserRole() {
        UserRole userRole = new UserRole();
        userRole.setName("super-pm-role");
        userRole.setDescription("super-pm-role");
        userRole.setUserPermissions(Set.of(
                UserPermission.MANAGE_PROJECTS,
                UserPermission.MANAGE_TASKS,
                UserPermission.ASSIGN_TEAM_MEMBER,
                UserPermission.UPDATE_TASK_STATE,
                UserPermission.SET_TASK_PRIORITY,
                UserPermission.ADD_ATTACHMENT
        ));
        return userRole;
    }

    private User getProjectManagerUser() {
        User user = new User();
        user.setUsername("project-manager@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("PMFirstname");
        user.setLastName("PMLastname");
        user.setUserType(UserType.PROJECT_MANAGER);
        return user;
    }

    private UserRole getTeamLeaderUserRole() {
        UserRole userRole = new UserRole();
        userRole.setName("super-tl-role");
        userRole.setDescription("super-tl-role");
        userRole.setUserPermissions(Set.of(
                UserPermission.MANAGE_PROJECTS,
                UserPermission.MANAGE_TASKS,
                UserPermission.ASSIGN_TEAM_MEMBER,
                UserPermission.UPDATE_TASK_STATE,
                UserPermission.SET_TASK_PRIORITY,
                UserPermission.ADD_ATTACHMENT
        ));
        return userRole;
    }

    private User getTeamLeaderUser() {
        User user = new User();
        user.setUsername("team-leader@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("TLFirstname");
        user.setLastName("TLLastname");
        user.setUserType(UserType.TEAM_LEADER);
        return user;
    }

    private UserRole getTeamMemberUserRole() {
        UserRole userRole = new UserRole();
        userRole.setName("super-tm-role");
        userRole.setDescription("super-tm-role");
        userRole.setUserPermissions(Set.of(
                UserPermission.UPDATE_TASK_STATE,
                UserPermission.ADD_ATTACHMENT
        ));
        return userRole;
    }

    private User getTeamMemberUser() {
        User user = new User();
        user.setUsername("team-member@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("TMFirstname");
        user.setLastName("TMLastname");
        user.setUserType(UserType.TEAM_MEMBER);
        return user;
    }
}

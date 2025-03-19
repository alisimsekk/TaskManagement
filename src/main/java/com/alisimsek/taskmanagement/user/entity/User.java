package com.alisimsek.taskmanagement.user.entity;

import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.role.entity.UserPermission;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import com.alisimsek.taskmanagement.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "\"user\"")
public class User extends BaseEntity implements UserDetails {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private boolean expired = false;

    private boolean locked = false;

    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @ManyToMany(mappedBy = "teamMembers")
    private List<Project> projects;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> assignedTasks;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_member",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id")
    )
    private Set<UserRole> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(userType.name()));
        Set<UserRole> activeRoles = roles.stream()
                .filter(role -> EntityStatus.ACTIVE == role.getEntityStatus())
                .collect(Collectors.toSet());
        authorities.addAll(activeRoles.stream()
                .flatMap(role -> role.getUserPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    public Set<UserPermission> getUserPermissions() {
        return roles.stream()
                .filter(role -> role.getEntityStatus() == EntityStatus.ACTIVE)
                .flatMap(role -> role.getUserPermissions().stream())
                .collect(Collectors.toSet());
    }

    public void addRole(UserRole userRole) {
        this.roles.add(userRole);
    }

    public void enabled() {
        this.enabled = true;
    }

    public void disabled() {
        this.enabled = false;
    }
}

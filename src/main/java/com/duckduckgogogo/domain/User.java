package com.duckduckgogogo.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "FACE_USERS")
public class User implements UserDetails, Serializable {
    /**
     *
     * */
    public final static String ROLE_ADMINISTRATOR = "A";
    /**
     *
     * */
    public final static String ROLE_CUSTOMER = "C";
    /**
     *
     * */
    public final static String ROLE_SUPPLIER = "S";
    /**
     *
     * */
    public final static String ROLE_PROJECT_MANAGEMENT = "PM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "USERNAME", length = 30, unique = true)
    private String username;

    @Column(name = "FIRST_NAME", length = 30)
    private String firstName;

    @Column(name = "LAST_NAME", length = 30)
    private String lastName;

    @Column(name = "EMAIL", length = 200, unique = true)
    private String email;

    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "ROLE", length = 2)
    private String role;

    @Column(name = "ENABLED")
    private boolean enabled;

    @Version
    @Column(name = "version")
    private int version;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null) this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        if (version != null) this.version = version;
    }

    public User converter() {
        User user = new User();

        user.setId(this.id);
        user.setUsername(this.username.trim().toLowerCase());
        user.setFirstName(this.firstName.trim());
        user.setLastName(this.lastName.trim());
        user.setEmail(this.email.trim().toLowerCase());
        user.setPassword(this.password.trim());
        user.setRole(this.role);
        user.setEnabled(this.enabled);
        user.setVersion(this.version);

        return user;
    }

    public boolean equals(User user) {
        if (user == this) return true;

        return this.id == user.getId();
    }
}

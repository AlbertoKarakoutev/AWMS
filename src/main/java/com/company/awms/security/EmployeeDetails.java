package com.company.awms.security;

import com.company.awms.data.employees.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class EmployeeDetails implements UserDetails {

    private String username;
    private String password;
    private String role;
    private String ID;
    private String firstName;
    private String lastName;

    EmployeeDetails(Employee employee) {
        this.username = employee.getEmail();
        this.password = employee.getPassword();
        this.role = employee.getRole();
        this.ID = employee.getID();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    public String getID() {
        return ID;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}

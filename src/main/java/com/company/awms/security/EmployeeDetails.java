package com.company.awms.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.company.awms.modules.base.employees.data.Employee;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("serial")
public class EmployeeDetails implements UserDetails {

    private String username;
    private String password;
    private String role;
    private String ID;
    private String nationalID;
    private String firstName;
    private String lastName;

    EmployeeDetails(Employee employee) {
        this.username = employee.getEmail();
        this.password = employee.getPassword();
        this.role = employee.getRole();
        this.ID = employee.getID();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.nationalID = employee.getNationalID();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getID() {
        return this.ID;
    }
    
    public String getRole() {
    	return this.role;
    }
    
    public String getNationalID() {
    	return this.nationalID;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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

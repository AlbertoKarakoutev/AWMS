package com.company.awms.security;

import com.company.awms.data.employees.Employee;
import com.company.awms.data.employees.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private EmployeeRepo employeeRepo;

    @Autowired
    public CustomUserDetailsService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<Employee> employee = this.employeeRepo.findByUsername(username);

        if(employee.isEmpty()){
            throw new UsernameNotFoundException("Employee with username " + username + " doesn't exist");
        }

        return new CustomUserDetails(employee.get());
    }
}

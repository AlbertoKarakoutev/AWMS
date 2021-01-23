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
public class EmployeeDetailsService implements UserDetailsService {

    private EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeDetailsService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Employee> employee = this.employeeRepo.findByEmail(email);

        if(employee.isEmpty()){
            throw new UsernameNotFoundException("Employee with email " + email + " doesn't exist");
        }

        return new EmployeeDetails(employee.get());
    }
}

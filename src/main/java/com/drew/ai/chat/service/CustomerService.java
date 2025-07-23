package com.drew.ai.chat.service;

import com.drew.ai.chat.entity.CustomerEntity;
import com.drew.ai.chat.entity.CustomerRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Tool(description = "Returns a list of all customers")
    public List<CustomerEntity> findAll() {
        return customerRepository.findAll();
    }

    @Tool(description = "Returns a single customer with the given ID, but returns an empty result if the customer does not exist")
    public Optional<CustomerEntity> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Tool(description = "Saves changes to an existing customer and returns the saved customer")
    public CustomerEntity save(CustomerEntity customer) {
        return customerRepository.save(customer);
    }

    @Tool(description = "Creates and saves a new customer with the given first name, last name, and email, and returns the saved customer")
    public CustomerEntity addNewCustomer(String firstName, String lastName, String email) {
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        return save(customer);
    }

    @Tool(description = "Deletes a customer with the given ID")
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}

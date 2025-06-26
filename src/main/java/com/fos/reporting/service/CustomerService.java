package com.fos.reporting.service;

import com.fos.reporting.domain.CustomerDto;
import com.fos.reporting.entity.Customer;
import com.fos.reporting.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Persists a new customer by combining first + last name.
     * @return true if saved successfully (ID > 0), false otherwise
     */
    @Transactional
    public boolean addCustomer(CustomerDto dto) {
        Customer c = new Customer();
        c.setCustomerName(dto.getCustomerFirstName().trim() + " " + dto.getCustomerLastName().trim());
        c.setCustomerEmail(dto.getCustomerEmail());
        c.setCustomerPhoneNumber(dto.getCustomerPhoneNumber());
        c.setVehicleNumber(dto.getVehicleNumber());

        Customer saved = customerRepository.save(c);
        // getCustomerId() is an int; check > 0 instead of comparing to null
        return saved.getCustomerId() > 0;
    }

    /**
     * Returns all customers as DTOs.
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream()
            .map(entity -> {
                CustomerDto dto = new CustomerDto();
                // DTO customerId is Long, so box the int into a Long
                dto.setCustomerId(Long.valueOf(entity.getCustomerId()));


                String[] parts = entity.getCustomerName().split(" ", 2);
                dto.setCustomerFirstName(parts[0]);
                dto.setCustomerLastName(parts.length > 1 ? parts[1] : "");

                dto.setCustomerEmail(entity.getCustomerEmail());
                dto.setCustomerPhoneNumber(entity.getCustomerPhoneNumber());
                dto.setVehicleNumber(entity.getVehicleNumber());
                return dto;
            })
            .collect(Collectors.toList());
    }
}

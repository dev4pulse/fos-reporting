package com.fos.reporting.service;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowerService {
    @Autowired
    private BorrowerRepository borrowerRepository;

    public Borrower createBorrower(BorrowerDto dto) {
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(dto, borrower);
        return borrowerRepository.save(borrower);
    }

    public List<Borrower> listBorrowers() {
        return borrowerRepository.findAll();
    }

    public Borrower updateBorrower(Long id, BorrowerDto dto) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));
        BeanUtils.copyProperties(dto, borrower);
        borrower.setId(id); // Make sure ID stays consistent
        return borrowerRepository.save(borrower);
    }

    public List<Borrower> findByCustomerName(String customerName) {
        return borrowerRepository.findByCustomerName(customerName);
    }

}


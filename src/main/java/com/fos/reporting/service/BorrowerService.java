package com.fos.reporting.service;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.repository.BorrowerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BorrowerService {
    @Autowired
    private BorrowerRepository borrowerRepository;

    public Borrower createBorrower(BorrowerDto dto) {
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(dto, borrower);
        borrower.setBorrowedAt(LocalDateTime.now());
        return borrowerRepository.save(borrower);
    }
}

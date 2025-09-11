package com.fos.reporting.service;
import com.fos.reporting.domain.BorrowerHistoryDto;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.entity.BorrowerHistory;
import com.fos.reporting.repository.BorrowerHistoryRepository;
import com.fos.reporting.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowerHistoryService {

    private final BorrowerHistoryRepository historyRepository;
    private final BorrowerRepository borrowerRepository;

    @Autowired
    public BorrowerHistoryService(BorrowerHistoryRepository historyRepository, BorrowerRepository borrowerRepository) {
        this.historyRepository = historyRepository;
        this.borrowerRepository = borrowerRepository;
    }


    //Save borrower history for any transaction (borrow, due paid, extra borrow)

    public void saveHistory(Borrower borrower, Double duePaid, Double extraBorrowed) {
        BorrowerHistory history = new BorrowerHistory();
        history.setBorrower(borrower);
        history.setCustomerName(borrower.getCustomerName());
        history.setCustomerVehicle(borrower.getCustomerVehicle());
        history.setEmployeeId(borrower.getEmployeeId());
        history.setAmountBorrowed(borrower.getAmountBorrowed());
        history.setBorrowDate(borrower.getBorrowDate());
        history.setDueDate(borrower.getDueDate());
        history.setStatus(borrower.getStatus());
        history.setPhone(borrower.getPhone());
        history.setDuePaid(duePaid);
        history.setExtraBorrowed(extraBorrowed);
        history.setUpdatedAt(LocalDateTime.now());

        // ✅ Always calculate remaining consistently
        history.setRemainingAmount(calculateRemaining(
                borrower.getAmountBorrowed(),
                extraBorrowed,
                duePaid
        ));

        historyRepository.save(history);
    }

    //Get borrower history by borrower ID.

    public List<BorrowerHistoryDto> getHistory(Long borrowerId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found with id: " + borrowerId));

        return historyRepository.findByBorrowerOrderByUpdatedAtDesc(borrower)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Search borrower history by customer name.
     */
    public List<BorrowerHistoryDto> getHistoryByCustomerName(String customerName) {
        return historyRepository.findByCustomerNameContainingIgnoreCaseOrderByUpdatedAtDesc(customerName)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all borrower history records.
     */
    public List<BorrowerHistoryDto> getAllBorrowersHistory() {
        return historyRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    //  Private helper methods

    /**
     * Consistent formula to calculate remaining amount.
     */
    private double calculateRemaining(Double amountBorrowed, Double extraBorrowed, Double duePaid) {
        double borrowed = (amountBorrowed != null ? amountBorrowed : 0.0);
        double extra = (extraBorrowed != null ? extraBorrowed : 0.0);
        double paid = (duePaid != null ? duePaid : 0.0);
        return borrowed + extra - paid;
    }

    /**
     * Convert Entity -> DTO
     */
    private BorrowerHistoryDto mapToDto(BorrowerHistory h) {
        BorrowerHistoryDto dto = new BorrowerHistoryDto();
        dto.setId(h.getId());
        dto.setBorrowerId(h.getBorrower().getId());
        dto.setCustomerName(h.getCustomerName());
        dto.setCustomerVehicle(h.getCustomerVehicle());
        dto.setEmployeeId(h.getEmployeeId());
        dto.setAmountBorrowed(h.getAmountBorrowed());
        dto.setBorrowDate(h.getBorrowDate());
        dto.setDueDate(h.getDueDate());
        dto.setStatus(h.getStatus());
        dto.setPhone(h.getPhone());
        dto.setDuePaid(h.getDuePaid());
        dto.setExtraBorrowed(h.getExtraBorrowed());
        dto.setUpdatedAt(h.getUpdatedAt());
        dto.setRemainingAmount(h.getRemainingAmount());
        return dto;
    }
}

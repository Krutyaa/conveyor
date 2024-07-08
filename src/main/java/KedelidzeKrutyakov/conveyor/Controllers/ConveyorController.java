package KedelidzeKrutyakov.conveyor.Controllers;

import KedelidzeKrutyakov.conveyor.DTO.*;
import KedelidzeKrutyakov.conveyor.Service.*;

import KedelidzeKrutyakov.conveyor.Service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
public class ConveyorController {

    private final LoanService loanService;
    private final ValidationService validationService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO request) {
        validationService.validateLoanApplicationRequest(request);
        return loanService.generateLoanOffers(request);
    }

    @PostMapping("/calculation")
    public CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringData) {
        return loanService.calculateCredit(scoringData);
    }
}
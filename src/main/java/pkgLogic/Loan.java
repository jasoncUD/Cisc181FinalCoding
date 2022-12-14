package pkgLogic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.formula.functions.FinanceLib;

public class Loan {
	private double LoanAmount;
	private double LoanBalanceEnd;
	private double InterestRate;
	private int LoanPaymentCnt;
	private boolean bCompoundingOption;
	private LocalDate StartDate;
	private double AdditionalPayment;
	private double Escrow;

	private HashMap<Integer,Double> hmRates = new HashMap<Integer,Double>();
	
	private ArrayList<Payment> loanPayments = new ArrayList<Payment>();

	public Loan(double loanAmount, double interestRate, int numYears, LocalDate startDate,
			double additionalPayment, double escrow
			, int adjustLockYears, int adjustLoanYears, double adjustLoanRate) {
		super();
			
		int loanPaymentCnt = numYears*12;
		
		double tmpInterestRate = interestRate;
		int tmpAdjustLockYears = adjustLockYears;
		
		System.out.println("start hmRate.put");
		for (int i = 1; i<=loanPaymentCnt; i++) {
			if ((adjustLoanYears != 0) && (adjustLoanRate != 0)) {
				if (i > (tmpAdjustLockYears * 12)) {
					System.out.println("i > (tmpAdjustLockYears * 12");
					tmpInterestRate += adjustLoanRate;
					tmpAdjustLockYears += adjustLoanYears;
				}
			}

			hmRates.put(i, tmpInterestRate);
			System.out.println("hmRates.put(" + i + "," + tmpInterestRate);
		}
		System.out.println("end hmRate.put");
		
		
		LoanAmount = loanAmount;
		InterestRate = interestRate;
		LoanPaymentCnt = loanPaymentCnt;
		StartDate = startDate;
		AdditionalPayment = additionalPayment;
		bCompoundingOption = false;
		LoanBalanceEnd = 0;
		this.Escrow = escrow;

		double RemainingBalance = LoanAmount;
		int PaymentCnt = 1;
		
		while (RemainingBalance >= this.GetPMT() + this.AdditionalPayment) {
			this.InterestRate = this.getInterestRate(PaymentCnt);
			try {
				Payment payment = new Payment(RemainingBalance, PaymentCnt++, startDate, this, false);
				RemainingBalance = payment.getEndingBalance(); 
				startDate = startDate.plusMonths(1);
				loanPayments.add(payment);
			} catch (Exception e) {
				System.out.println("exception for payment " + PaymentCnt);
			}

		}
		System.out.println("finished adding payments");
		
		Payment payment = new Payment(RemainingBalance, PaymentCnt++, startDate, this, true);
		loanPayments.add(payment);
	}

	public double GetPMT() {
        double PMT = 0;
        PMT = FinanceLib.pmt(this.InterestRate/12,
                this.LoanPaymentCnt,
                this.LoanAmount,
                this.LoanBalanceEnd,
                this.bCompoundingOption);
        return Math.abs(PMT);
    }

    public double getTotalPayments() {
        double tot = 0;
        for (Payment val:this.loanPayments) {
        	System.out.println("val.getPayment(): " + val.getPayment());
            tot += val.getPayment();
        }
        System.out.println("total: " + tot);
        return tot;
    }

    public double getTotalInterest() {

        double interest = 0;
        for (Payment val:this.loanPayments) {
            interest += val.getInterestPayment();
        }
        return interest;

    }

	public double getTotalEscrow() {

		double escrow = 0;
		for (Payment p : this.loanPayments) {
			escrow += p.getEscrowPayment();
		}
		return escrow;

	}

	public double getLoanAmount() {
		return LoanAmount;
	}

	public void setLoanAmount(double loanAmount) {
		LoanAmount = loanAmount;
	}

	public double getLoanBalanceEnd() {
		return LoanBalanceEnd;
	}

	public void setLoanBalanceEnd(double loanBalanceEnd) {
		LoanBalanceEnd = loanBalanceEnd;
	}

	public double getInterestRate(int PaymentNbr) {
		
		return this.hmRates.get(PaymentNbr);
	}

	public void setInterestRate(double interestRate) {
		InterestRate = interestRate;
	}

	public int getLoanPaymentCnt() {
		return LoanPaymentCnt;
	}

	public void setLoanPaymentCnt(int loanPaymentCnt) {
		LoanPaymentCnt = loanPaymentCnt;
	}

	public boolean isbCompoundingOption() {
		return bCompoundingOption;
	}

	public void setbCompoundingOption(boolean bCompoundingOption) {
		this.bCompoundingOption = bCompoundingOption;
	}

	public LocalDate getStartDate() {
		return StartDate;
	}

	public void setStartDate(LocalDate startDate) {
		StartDate = startDate;
	}

	public double getAdditionalPayment() {
		return AdditionalPayment;
	}

	public void setAdditionalPayment(double additionalPayment) {
		AdditionalPayment = additionalPayment;
	}

	public ArrayList<Payment> getLoanPayments() {
		return loanPayments;
	}

	public void setLoanPayments(ArrayList<Payment> loanPayments) {
		this.loanPayments = loanPayments;
	}

	public double getEscrow() {
		return Escrow;
	}

}
package com.design.pattern.behavioural.Mediator;

/*
 * A mediator design pattern is one of the important and widely used behavioral design patterns. 
 * Mediator enables the decoupling of objects by introducing a layer in between so that the 
 * interaction between objects happens via the layer
 * 
 * The facade exposes existing functionality and the mediator adds to the existing functionality.
 * 
 * Similar To: Facade Design Pattern
 */
public class MediatorDesignPatten {

    public static void main(String[] args) {
        KYCVerification kycVerification = new KYCVerification("KWDSJ76");
        kycVerification.simulateKycVerification();
    }
}

interface Mediator {
    void convey();
}

class KYCVerification implements Mediator {
    private BasicInfoVerification basicInfoVerification = new BasicInfoVerification(this);
    private AddressVerification addressVerification = new AddressVerification(this);
    private AccountVerification accountVerification = new AccountVerification(this);
    private String userId;

    public KYCVerification(String userId) {
        this.userId = userId;
    }

    public void simulateKycVerification() {
        basicInfoVerification.verify(userId);
        addressVerification.verify(userId);
        accountVerification.verify(userId);
    }

    public boolean isKycSuccess() {
        return (basicInfoVerification.isBasicInfoVerified() && addressVerification.isAddressVerified()
            && accountVerification.isAccountVerified());
    }

    @Override
    public void convey() {
        System.out.println("is KYC Successful: " + (isKycSuccess() ? "Yes" : "No"));
    }
}

interface VerificationStep {
    void verify(String userId);
}

class BasicInfoVerification implements VerificationStep {
    private Mediator mediator;
    private boolean basicInfoVerified = false;

    public BasicInfoVerification(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void verify(String userId) {
        System.out.println("Verifying User Basic Info with Id: " + userId);
        this.basicInfoVerified = true;
        mediator.convey();
    }

    public boolean isBasicInfoVerified() {
        return basicInfoVerified;
    }
}

class AddressVerification implements VerificationStep {
    private Mediator mediator;
    private boolean adressVerified = false;

    public AddressVerification(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void verify(String userId) {
        System.out.println("Verifying User Address with Id: " + userId);
        this.adressVerified = true;
        mediator.convey();
    }

    public boolean isAddressVerified() {
        return adressVerified;
    }
}

class AccountVerification implements VerificationStep {
    private Mediator mediator;
    private boolean accountVerified = false;

    public AccountVerification(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void verify(String userId) {
        System.out.println("Verifying User Account with Id: " + userId);
        this.accountVerified = true;
        mediator.convey();
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }
}

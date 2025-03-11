package com.design.pattern.behavioural.state;

/*
 * When an object modifies its behavior according to its internal state, the state design pattern is 
 * applied.
 * 
 * Similar To: -
 */
public class StateDesignPattern {

    public static void main(String[] args) {
        AtmMachine atmMachine = new AtmMachine();
        atmMachine.deposit(500);
        atmMachine.withdraw(1000);
        System.out.println("available amount: " + atmMachine.getAvailableAmount());
        atmMachine.withdraw(1000);
        atmMachine.withdraw(1000);

        atmMachine.deposit(500);
        atmMachine.withdraw(200);
        System.out.println("available amount: " + atmMachine.getAvailableAmount());
    }
}

interface ATMState {
    void withdraw(int amount);

    void deposit(int amount);
}

class Working implements ATMState {
    private AtmMachine atm;

    public Working(AtmMachine atm) {
        this.atm = atm;
    }

    @Override
    public void withdraw(int amount) {
        boolean isEmpty = false;
        int availableAmount = atm.getAvailableAmount();
        int updatedAmount = 0;
        if (amount > availableAmount) {
            updatedAmount = availableAmount;
            isEmpty = true;
            atm.setAvailableAmount(0);
            System.out.println("Partial amount " + updatedAmount + " has been withdrawn");
        } else {
            updatedAmount = availableAmount - amount;
            atm.setAvailableAmount(updatedAmount);
            System.out.println(amount + " has been withdrawn");
        }
        if (isEmpty) {
            atm.setState(new OutOfCash(atm));
        }
    }

    @Override
    public void deposit(int amount) {
        System.out.println(amount + " deposited");
        atm.setAvailableAmount(atm.getAvailableAmount() + amount);
    }
}

class OutOfCash implements ATMState {
    private AtmMachine atm;

    public OutOfCash(AtmMachine atm) {
        this.atm = atm;
    }

    @Override
    public void withdraw(int amount) {
        System.out.println("ATM out of cash!");
    }

    @Override
    public void deposit(int amount) {
        System.out.println(amount + " deposited");
        atm.setAvailableAmount(atm.getAvailableAmount() + amount);
        atm.setState(new Working(atm));
    }
}

class AtmMachine implements ATMState {
    private ATMState currentState;
    private int availableAmount;

    public AtmMachine() {
        currentState = new Working(this);
        availableAmount = 1000;
    }

    public void setState(ATMState atmState) {
        this.currentState = atmState;
    }

    @Override
    public void withdraw(int amount) {
        currentState.withdraw(amount);
    }

    @Override
    public void deposit(int amount) {
        currentState.deposit(amount);
    }

    public int getAvailableAmount() {
        return this.availableAmount;
    }

    public void setAvailableAmount(int amount) {
        this.availableAmount = amount;
    }
}

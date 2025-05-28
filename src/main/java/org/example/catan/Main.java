package org.example.catan;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank(10, 4);
        System.out.println(bank.spendResources(2, "Stone").size());
        System.out.println(bank.spendResources(2, "Stone").get(0).name);
    }
}

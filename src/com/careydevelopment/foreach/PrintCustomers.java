package com.careydevelopment.foreach;

import java.util.ArrayList;
import java.util.List;

import com.careydevelopment.tacklestore.entity.Customer;

public class PrintCustomers {

    public static final void main(String[] args) {
        //get the list of customers
        List<Customer> customers = getCustomers();
        
        //let's print out all customers
        System.out.println("Printing all customers");
        customers.forEach(customer->System.out.println(customer));
        
        //let's print customers over the age of 25
        System.out.println("\nPrinting all customers over the age of 25");
        customers.forEach(customer -> {
            if (customer.getAge() > 25) {
                System.out.println(customer);
            }
        });
        
        System.out.println("\nPrinting all customers with a method reference");
        customers.forEach(System.out::println);
    }
    
    
    private static List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<Customer>();
        
        customers.add(new Customer(1000l,"John","Smith",25));
        customers.add(new Customer(1001l,"Jane","Doe",36));
        customers.add(new Customer(1002l,"Jerry","Tyne",20));
        customers.add(new Customer(1003l,"Glenn","First",29));
        customers.add(new Customer(1004l,"Beth","Abbey",35));

        return customers;
    }
}

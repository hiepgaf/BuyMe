package app.bennsandoval.com.woodmin.interfaces;

import app.bennsandoval.com.woodmin.models.customers.Customer;

/**
 * Created by Mackbook on 1/5/15.
 */
public interface CustomerActions {
    void sendEmail(Customer customer);
    void makeACall(Customer customer);
}

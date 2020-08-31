package com.sunfintech.consumer.entity;

public class Order {
    
    private User user;

    private Commodity commodity;
    
    private Business business;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return "Order [user=" + user + ", commodity=" + commodity + ", business=" + business + "]";
    }

}

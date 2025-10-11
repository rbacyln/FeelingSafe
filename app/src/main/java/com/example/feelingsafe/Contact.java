package com.example.feelingsafe;

/**
 * TR: Bu sınıf, tek bir acil durum kişisinin verilerini tutan basit bir veri modelidir (POJO).
 * EN: This class is a simple data model that holds the data for a single emergency contact.
 */
public class Contact {
    private String name;
    private String phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
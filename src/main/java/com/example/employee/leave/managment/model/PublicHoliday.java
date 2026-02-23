package com.example.employee.leave.managment.model;




import java.time.LocalDate;

public class PublicHoliday {
    private LocalDate date;
    private String    name;
    private String    localName;
    private String    type;

    public PublicHoliday() {}

    public PublicHoliday(LocalDate date, String name, String localName, String type) {
        this.date      = date;
        this.name      = name;
        this.localName = localName;
        this.type      = type;
    }

    // ── Getters & Setters ──
    public LocalDate getDate()      { return date; }
    public void setDate(LocalDate d){ this.date = d; }

    public String getName()         { return name; }
    public void setName(String n)   { this.name = n; }

    public String getLocalName()          { return localName; }
    public void setLocalName(String ln)   { this.localName = ln; }

    public String getType()         { return type; }
    public void setType(String t)   { this.type = t; }

    // ── Helpers used by Thymeleaf ──
    public int getDay()   { return date != null ? date.getDayOfMonth() : 0; }
    public String getMonthShort() {
        if (date == null) return "";
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        return months[date.getMonthValue() - 1];
    }
}
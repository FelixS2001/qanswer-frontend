package com.example.felix.qanswer.Models;

/**
 * Enum for the categories
 * @author Sebastian
 */
public enum Categories
{
    ALL("Kein Filter"),
    BSP("BSP"),
    BWM("BWM"),
    DBI("DBI"),
    DEUTSCH("Deutsch"),
    ENGLISH("English"),
    GGP("GGP"),
    MATHEMATIK("Mathematik"),
    NAWI("NAWI"),
    NVS("NVS"),
    POS("POS"),
    SYP("SYP"),
    TC1("TC1");

    String category;

    Categories(String filter)
    {
        category = filter;
    }

    public String showCategory()
    {
        return category;
    }
}

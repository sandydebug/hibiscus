package com.anmol.hibiscus.Rasoi.Model;

/**
 * Created by anmol on 10/18/2017.
 */

public class mess1 {

    String date,brkfast,lnch,dinnr,bs,ls,ds;
    Long bp,lp,dp;

    public mess1() {
    }

    public mess1(String date, String brkfast, String lnch, String dinnr, String bs, String ls, String ds, Long bp,Long lp, Long dp) {
        this.date = date;
        this.brkfast = brkfast;
        this.lnch = lnch;
        this.dinnr = dinnr;
        this.bs = bs;
        this.ls = ls;
        this.ds = ds;
        this.lp = lp;
        this.bp = bp;
        this.dp = dp;

    }

    public Long getBp() {
        return bp;
    }

    public void setBp(Long bp) {
        this.bp = bp;
    }

    public Long getLp() {
        return lp;
    }

    public void setLp(Long lp) {
        this.lp = lp;
    }

    public Long getDp() {
        return dp;
    }

    public void setDp(Long dp) {
        this.dp = dp;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public String getLs() {
        return ls;
    }

    public void setLs(String ls) {
        this.ls = ls;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getDay() {
        return date;
    }

    public void setDay(String date) {
        this.date = date;
    }

    public String getBrkfast() {
        return brkfast;
    }

    public void setBrkfast(String brkfast) {
        this.brkfast = brkfast;
    }

    public String getLnch() {
        return lnch;
    }

    public void setLnch(String lnch) {
        this.lnch = lnch;
    }

    public String getDinnr() {
        return dinnr;
    }

    public void setDinnr(String dinnr) {
        this.dinnr = dinnr;
    }
}

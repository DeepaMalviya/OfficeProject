package com.daffodil.officeproject.LoginModule;

/**
 * Created by Daffodil on 6/29/2018.
 */

public class LoginModel {

    //username,pwd aur device id dena

    String userId, mobile, otp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

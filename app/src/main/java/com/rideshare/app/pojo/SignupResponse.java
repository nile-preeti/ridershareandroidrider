package com.rideshare.app.pojo;

public class SignupResponse {
    private boolean status;
    private String message;
    private UserData data;
    private String token;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SignupResponseModel{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", token='" + token + '\'' +
                '}';
    }

    public static class UserData {
        private int user_id;
        private String utype;
        private String username;
        private String email;
        private String country_code;
        private String mobile;
        private String otp;
        private String status;
        private boolean is_card;
        private boolean add_card;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUtype() {
            return utype;
        }

        public void setUtype(String utype) {
            this.utype = utype;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCountry_code() {
            return country_code;
        }

        public void setCountry_code(String country_code) {
            this.country_code = country_code;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isIs_card() {
            return is_card;
        }

        public void setIs_card(boolean is_card) {
            this.is_card = is_card;
        }

        public boolean isAdd_card() {
            return add_card;
        }

        public void setAdd_card(boolean add_card) {
            this.add_card = add_card;
        }

        @Override
        public String toString() {
            return "UserData{" +
                    "user_id=" + user_id +
                    ", utype='" + utype + '\'' +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", country_code='" + country_code + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", otp='" + otp + '\'' +
                    ", status='" + status + '\'' +
                    ", is_card=" + is_card +
                    ", add_card=" + add_card +
                    '}';
        }
    }
}

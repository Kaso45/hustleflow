package com.hustleflow.authentication.dto; // <--- Kiểm tra xem file của bạn có đúng là dòng này không?

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String password;
    private String email;
}

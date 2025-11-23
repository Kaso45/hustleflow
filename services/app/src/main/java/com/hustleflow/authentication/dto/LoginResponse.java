package com.hustleflow.authentication.dto; // <--- Kiểm tra kỹ dòng này

import lombok.AllArgsConstructor; // <--- Phải có cái này
import lombok.Data;               // <--- Phải có cái này
import lombok.NoArgsConstructor;  // <--- Nên có thêm cái này

@Data
@AllArgsConstructor // <--- QUAN TRỌNG: Để sửa lỗi constructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private long expiresIn;
}

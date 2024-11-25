package com.web.sso.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    private String email;
    private String name;
    private String family_name;
    private String password;
}

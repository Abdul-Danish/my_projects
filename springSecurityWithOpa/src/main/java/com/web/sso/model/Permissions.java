package com.web.sso.model;

import java.util.ArrayList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Permissions {

    private ArrayList<Map<String, String>> permission;
}

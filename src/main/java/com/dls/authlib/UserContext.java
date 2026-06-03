package com.dls.authlib;

import java.util.List;

public record UserContext(String userId, List<String> roles, String email) {
}

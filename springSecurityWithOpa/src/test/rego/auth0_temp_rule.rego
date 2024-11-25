package auth0.rule

import future.keywords.if


default authorized := false

permissions_map := {
	"Solution-Designer": [{"permission": "GET:solutions"},{"permission": "POST:solutions"}],
    "User-Management": [{"permission": "GET:users"}, {"permission": "POST:users"}]
}


authorized if {
	allow["permission_Granted"]
}


decoded_jwt := result {
	result := io.jwt.decode(input.access_token)
}

allow["permission_Granted"] if {
	map := permissions_map[decoded_jwt[_]["user_roles"][_]][_]
    
    permissions := map["permission"]
    
    permissions == input.action
}

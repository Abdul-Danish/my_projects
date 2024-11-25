
package rbac.rule

import future.keywords.if
import future.keywords.in


default allow := false

# allow if user_is_admin
# user_is_admin if "admin" in input["roles"]


role_permissions := {
	"admin" : [{"action": "read"}, {"action": "create"}, {"action": "update"}],
	"user" : [{"action": "read"}]
}

api_permissions := {
	"GET": ["read"],
    "POST": ["create", "update"],
    "PUT": ["update"]
}


# allow if {
# 	roles := input["roles"][_]
# 	
# 	permissions := role_permissions[roles]
#     
#  	allowed_actions = permissions[_]
#     
# #     input.actions[_] in allowed_actions
#     allowed_actions == {"action": input.action}
# }


allow if {
	required_api_permission := api_permissions[input.method]
    
    roles := input["roles"][_]
	required_role_permission := role_permissions[roles]
    
 	allowed_actions = required_role_permission[_]
    
	allowed_actions == {"action": required_api_permission[_]}
}


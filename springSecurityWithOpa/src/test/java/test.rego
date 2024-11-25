#
# Simple authorization rule for accounts
#
# Assumes an input document with the following properties:
#
# resource: requested resource
# method: request method
# authorities: Granted authorities
# headers: Request headers
#
package auth.user

# Not authorized by default
default authorized = false

# Authorize when there are no rules that deny access to the resource and
# there's at least one rule allowing
authorized = true {
    count(deny) == 0
    count(allow) > 0
}

# Allow access to /public
allow["public"] {
    regex.match("^/user/.*",input.uri)
}

# Account API requires authenticated user
deny["user_authenticated"] {
    regex.match("^/user/.*",input.uri)
    regex.match("ANONYMOUS",input.principal)
}

deny["Invalid_Role"] {
	not is_user
}

is_user {
	some i
    input.authorities[i] == "ROLE_user"
}

# Authorize access to account if principal has
# matching authority

allow["user_authorized"] {
	regex.match("/admin/", input.uri)
    is_admin
}

is_admin {
	some i
    input.authorities[i] == "ROLE_admin"
}

# allow["user_api_authorized"] {
#     regex.match("^/admin/.+",input.uri)
#     parts := split(input.uri,"/")
#     user_id := parts[2]
#     role := concat(":",[ "ROLE_admin", "read", user_id] )
#     role == input.authorities[i]
# }

# What is a feature flag?
A Feature flag or a Feature toggle, is a mechanism that allows to activate or deactivate functionality
without deploying again the application.

You use it for:
- Progressive realeses
- Activate features per region
- A/B testing
- Deactivate functionalities without rollback

# Scope
Build a centralized Feature Flag Service that allows:
1. Create, update, delte feature flags
2. Evaluate if a feature is active for an user
3. Support:
    - Global activation
    - Rol based activation
    - Percent activation
4. 
5. Rest API
6 Cache

# What is this project?
A service that enables other parts of the system to do this:

``` java
boolean enabled = featureFlagService.isEnabled("new-checkout", context);
```


# Type of rules
1. Global toggle
```
enabled: true | false
```

2. Role rule
```
roles: ["ADMIN", "MANAGER"]
```

3. User rule
userIds: [UUID, UUID]

4. Percentage rule
```
rolloutPercentage: 0-100
```
and then 

hash(userId) % 100 < rolloutPercentage

# How does this rules combine?
```
enabled == true
AND
(
    RoleRule
    OR
    UserRule
    OR
    PercentageRule
)
```

That means:
- IF enabled == false, then its always false
- IF enabled == true,
    - IF no rules, then global activation
    - If rules, then enough if one is true

----
POST   /api/features
PUT    /api/features/{key}
DELETE /api/features/{key}
GET    /api/features
GET    /api/features/{key}

# Errors
In the system there are 3 kinds of errors:
- Domain Exceptions
- Application Exceptions
- Technical Exceptions
# IAM User Management

Terraform that creates IAM users and IAM roles from a YAML list.

Users and console login profiles come from `user-roles.yaml`. Four roles are created (`readonly`, `admin`, `auditor`, `developer`) and attached to AWS managed policies. The YAML also lists which roles each user should assume; that mapping is parsed but not applied as IAM attachments yet.

## Files

- `users.tf` — users and login profiles
- `roles.tf` — roles and managed-policy attachments
- `user-roles.yaml` — usernames and intended roles
- `provider.tf`

## Example YAML

```yaml
users:
  - username: john
    roles: [readonly, developer]
```

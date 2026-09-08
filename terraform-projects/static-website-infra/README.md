# Static Website Infrastructure

Terraform for a private S3 bucket in front of CloudFront, with an ACM certificate in `us-east-1` and Route 53 records used for certificate validation.

This is an IaC lab, not a full production site pipeline. It does not upload objects to the bucket or create a Route 53 alias from a custom domain to CloudFront.

## Resources

- S3 bucket (versioning, public access blocked, AES-256)
- CloudFront distribution (OAC, HTTPS redirect, GET/HEAD)
- Bucket policy allowing that distribution
- ACM certificate and DNS validation records

## Learning focus

S3, CloudFront, OAC, ACM, DNS validation, bucket policies.

## Layout

Terraform lives in `src/`. Apply from there after setting the variables in `variables.tf`.

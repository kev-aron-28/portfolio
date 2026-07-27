locals {
  resource_prefix = "${var.project_name}-${var.environment}"
  bucket_name = "${local.resource_prefix}-website"
  default_tags = {
    Project = "Static website"
    Environment = "Dev"
    ManagedBy = "Terraform"
  }
}
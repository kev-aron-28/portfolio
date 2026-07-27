variable "region" {
  type = string
  default = "us-east-1"
}

variable "project_name" {
  description = "Project made used for naming aws_resources"
  type = string
  default = "static-website"
}

variable "environment" {
  description = "Deployment environment"
  type = string
  default = "dev"

  validation {
    condition = contains(["dev", "qa", "prod"], var.environment)
    error_message = "Environment must be one of: dev, qa, prod"
  }
}

variable "domain_name" {
  description = "Primary domain name"
  type = string
}

variable "hosted_zone_id" {
  description = "Route53 Hosted Zone ID"
  type = string
}


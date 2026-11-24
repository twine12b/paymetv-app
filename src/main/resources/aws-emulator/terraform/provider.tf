terraform {
  backend "local" {
    path = "terraform.tfstate"
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      # AWS provider 6.23.0+ uses S3 Control for bucket tags. AWS Emulator
      # cannot currently resolve the account-ID-prefixed S3 Control hostname.
      version = "= 6.22.0"
    }
  }
}

provider "aws" {
  # Configuration options
  access_key                  = "AKIAIOSFODNN7EXAMPLE"
  secret_key                  = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
  region                      = "us-east-1"
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true

  endpoints {
    ec2       = "http://localhost:4566"
    eks       = "http://localhost:4566"
    ecr       = "http://localhost:4566"
    iam       = "http://localhost:4566"
    s3        = "http://localhost:4566"
    s3control = "http://localhost:4566"
    cloudwatch = "http://localhost:4566"
    cloudwatchlogs = "http://localhost:4566"
  }

  insecure = true
}

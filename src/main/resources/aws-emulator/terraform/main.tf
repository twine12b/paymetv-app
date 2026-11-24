# Terraform loads every .tf file in this directory as one root module,
# including the EKS configuration in k8s.tf.
resource "aws_s3_bucket" "b" {
  bucket = "pmtv-ml-dev-bucket12313"


  tags = {
    Name        = "ML bucket"
    Environment = "Dev"
  }
}

resource "aws_s3_bucket_versioning" "b" {
  bucket = aws_s3_bucket.b.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_acl" "b" {
  bucket = aws_s3_bucket.b.id
  acl    = "private"
}

resource "aws_s3_bucket" "a" {
  bucket = "pmtv-admin-dev-bucket12314"


  tags = {
    Name        = "ADMIN bucket"
    Environment = "Dev"
  }
}

resource "aws_s3_bucket_versioning" "a" {
  bucket = aws_s3_bucket.a.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_acl" "a" {
  bucket = aws_s3_bucket.a.id
  acl    = "private"
}
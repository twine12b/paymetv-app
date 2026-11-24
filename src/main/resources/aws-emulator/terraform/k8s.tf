resource "aws_vpc" "eks" {
  cidr_block           = "10.20.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "paymetv-eks-vpc"
  }
}

resource "aws_subnet" "eks" {
  count                   = 2
  vpc_id                  = aws_vpc.eks.id
  cidr_block              = "10.20.${count.index + 1}.0/24"
  availability_zone       = count.index == 0 ? "us-east-1a" : "us-east-1b"
  map_public_ip_on_launch = true

  tags = {
    Name                                = "paymetv-eks-subnet-${count.index + 1}"
    "kubernetes.io/cluster/paymetv-eks" = "shared"
  }
}

resource "aws_iam_role" "eks_cluster" {
  name = "paymetv-eks-cluster-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "eks.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "eks_cluster" {
  role       = aws_iam_role.eks_cluster.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
}

resource "aws_eks_cluster" "paymetv" {
  name     = "paymetv-eks"
  version  = "1.31"
  role_arn = aws_iam_role.eks_cluster.arn

  vpc_config {
    subnet_ids              = aws_subnet.eks[*].id
    endpoint_private_access = true
    endpoint_public_access  = true
  }

  tags = {
    Name        = "paymetv-eks"
    Environment = "Dev"
  }

  depends_on = [aws_iam_role_policy_attachment.eks_cluster]
}

output "eks_cluster_endpoint" {
  description = "Kubernetes API endpoint returned for the EKS cluster."
  value       = aws_eks_cluster.paymetv.endpoint
}

output "eks_cluster_certificate_authority_data" {
  description = "Base64-encoded cluster certificate authority data."
  value       = aws_eks_cluster.paymetv.certificate_authority[0].data
}

resource "kubernetes_secret" "ghcr" {

  metadata {

    name      = "ghcr-secret"
    namespace = kubernetes_namespace.of_fiap.metadata[0].name

  }

  type = "kubernetes.io/dockerconfigjson"

  data = {

    ".dockerconfigjson" = jsonencode({

      auths = {

        "ghcr.io" = {

          username = var.ghcr_username
          password = var.ghcr_token
          auth = base64encode("${var.ghcr_username}:${var.ghcr_token}")

        }

      }

    })

  }

}
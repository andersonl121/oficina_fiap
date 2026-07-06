resource "kubernetes_deployment" "postgres" {

  metadata {

    name      = "postgres"
    namespace = kubernetes_namespace.of_fiap.metadata[0].name

  }

  spec {

    replicas = 1

    selector {

      match_labels = {
        app = "postgres"
      }

    }

    template {

      metadata {

        labels = {
          app = "postgres"
        }

      }

      spec {

        container {

          name  = "postgres"
          image = "postgres:16"

          env {
            name  = "POSTGRES_DB"
            value = "oficina"
          }

          env {
            name  = "POSTGRES_USER"
            value = "postgres"
          }

          env {
            name  = "POSTGRES_PASSWORD"
            value = "postgres"
          }

          port {
            container_port = 5432
          }

        }

      }

    }

  }

}

resource "kubernetes_service" "postgres" {

  metadata {

    name      = "postgres"
    namespace = kubernetes_namespace.of_fiap.metadata[0].name

  }

  spec {

    selector = {
      app = "postgres"
    }

    port {

      port        = 5432
      target_port = 5432

    }

  }

}
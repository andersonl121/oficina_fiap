resource "kubernetes_deployment" "spring_api" {

  metadata {

    name      = "spring-api"
    namespace = kubernetes_namespace.of_fiap.metadata[0].name

  }

  spec {

    replicas = 2

    selector {

      match_labels = {
        app = "spring-api"
      }

    }

    template {

      metadata {

        labels = {
          app = "spring-api"
        }

      }

      spec {

        image_pull_secrets {

          name = kubernetes_secret.ghcr.metadata[0].name

        }

        container {

          name  = "spring-api"
          image = var.image_name

          port {

            container_port = 8080

          }

          env {

            name  = "SPRING_DATASOURCE_URL"
            value = "jdbc:postgresql://postgres:5432/oficina"

          }

          env {

            name  = "SPRING_DATASOURCE_USERNAME"
            value = "postgres"

          }

          env {

            name  = "SPRING_DATASOURCE_PASSWORD"
            value = "postgres"

          }

          readiness_probe {

            http_get {

              path = "/actuator/health/readiness"
              port = 8080

            }

            initial_delay_seconds = 240
            period_seconds = 30

          }

          liveness_probe {

            http_get {

              path = "/actuator/health/liveness"
              port = 8080

            }

            initial_delay_seconds = 300
            period_seconds = 30

          }

        }

      }

    }

  }

}

resource "kubernetes_service" "spring_api" {

  metadata {

    name      = "spring-api"
    namespace = kubernetes_namespace.of_fiap.metadata[0].name

  }

  spec {

    selector = {
      app = "spring-api"
    }

    port {

      port        = 8080
      target_port = 8080

    }

    type = "NodePort"

  }

}
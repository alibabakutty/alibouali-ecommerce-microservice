pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 90, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
        SPRING_CLOUD_CONFIG_ENABLED = 'false'
        SPRING_CONFIG_IMPORT = 'optional:configserver:'
    }

    stages {
        stage('Verify Java and Maven') {
            steps {
                sh '''
                    export JAVA_HOME=/opt/java/openjdk
                    export PATH="$JAVA_HOME/bin:$PATH"

                    echo "Java version:"
                    java -version

                    echo "Maven version:"
                    ./services/customer/mvnw --version
                '''
            }
        }

        stage('Docker Preflight') {
            steps {
                sh '''
                    set -eu
                    echo "Docker executable:"
                    command -v docker
                    echo "Docker version:"
                    docker version
                    echo "Docker Compose version:"
                    docker compose version
                    echo "Validating Compose configuration:"
                    docker compose config --quiet
                '''
            }
        }

        stage('Authentication Service') {
            steps {
                dir('services/authentication-service') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Config Server') {
            steps {
                dir('services/config-server') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Customer Service') {
            steps {
                dir('services/customer') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Discovery Service') {
            steps {
                dir('services/discovery') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Gateway Service') {
            steps {
                dir('services/gateway') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Notification Service') {
            steps {
                dir('services/notification') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Order Service') {
            steps {
                dir('services/order') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Payment Service') {
            steps {
                dir('services/payment') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }

        stage('Product Service') {
            steps {
                dir('services/product') {
                    sh 'export JAVA_HOME=/opt/java/openjdk; export PATH="$JAVA_HOME/bin:$PATH"; ./mvnw --batch-mode --no-transfer-progress clean verify'
                }
            }
        }
    }

    post {
        always {
            junit(
                testResults: 'services/**/target/surefire-reports/TEST-*.xml',
                allowEmptyResults: true
            )
        }

        success {
            echo 'All nine microservices passed Maven verification.'
        }

        failure {
            echo 'CI failed. Review the failed stage and test logs.'
        }
    }
}

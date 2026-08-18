pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
            }
        }

        stage('Maven Version') {
            steps {
                sh 'mvn -version'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('List Artifacts') {
            steps {
                sh 'ls -lh target/'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t jenkins-maven-demo:1.0 \
                      .
                '''
            }
        }

        stage('Docker Test') {
            steps {
                sh '''
                    docker rm -f jenkins-maven-demo 2>/dev/null || true

                    docker run --name jenkins-maven-demo \
                      jenkins-maven-demo:1.0

                    docker ps -a --filter name=jenkins-maven-demo
                '''
            }
        }
    }

    post {

        always {
            echo 'Maven + Docker pipeline finished.'
        }

        success {
            echo 'Maven build and Docker test successful!'
        }

        failure {
            echo 'Maven or Docker pipeline failed!'
        }
    }
}
